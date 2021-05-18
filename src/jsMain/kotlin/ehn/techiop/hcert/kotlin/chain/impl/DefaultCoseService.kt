package ehn.techiop.hcert.kotlin.chain.impl

import Buffer
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.crypto.Cose
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise
import kotlin.js.json

actual class DefaultCoseService(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        val header =
            json(*cryptoService.getCborHeaders().map { Pair(it.first.value.toString(), it.second) }.toTypedArray())
        val signer = cryptoService.getCborSigningKey()
        val promise = Cose.sign(header, input, signer)
        // TODO get result from promise, with suspend
        return byteArrayOf()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false

        val cborJson = Cbor.Decoder.decodeAllSync(Buffer(input.toUint8Array()))
        val cose = cborJson[0] as Cbor.Tagged
        val coseValue = cose.value as Array<Buffer>
        val protectedHeader = coseValue[0]
        val unprotectedHeader = coseValue[1]
        val content = coseValue[2]
        val signature = coseValue[3]

        // TODO: Can we get rid of dynamic here?
        val protectedHeaderCbor = Cbor.Decoder.decodeAllSync(protectedHeader)[0].asDynamic()

        val kid = protectedHeaderCbor.get(4) as Uint8Array? ?: if (unprotectedHeader.length !== undefined)
            // TODO: Does this work? will be confirmed by testcases!
            Cbor.Decoder.decodeAllSync(unprotectedHeader)[0].asDynamic().get(4) as Uint8Array
        else
            throw IllegalArgumentException("KID not found")
        if (kid === undefined)
            throw IllegalArgumentException("KID not found")
        val algorithm = protectedHeaderCbor.get(1)
        val pubKey = cryptoService.getCborVerificationKey(kid.toByteArray(), verificationResult)
        val result = Cose.verify(input, pubKey)
        // TODO make this a suspend function, and then provide a wrapper from JS to call it as a promise
        verificationResult.coseVerified = true
        return content.toByteArray()
    }

    actual companion object {
        actual fun getInstance(cryptoService: CryptoService): DefaultCoseService {
            return DefaultCoseService(cryptoService)
        }
    }
}

suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then({ cont.resume(it) }, { cont.resumeWithException(it) })
}
