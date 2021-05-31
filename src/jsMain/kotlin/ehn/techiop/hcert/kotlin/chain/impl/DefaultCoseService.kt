package ehn.techiop.hcert.kotlin.chain.impl

import Buffer
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.crypto.Cose
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise
import kotlin.js.json

actual class DefaultCoseService actual constructor(private val cryptoService: CryptoService) : CoseService {

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
        return jsTry {
            val cborJson = Cbor.Decoder.decodeAllSync(Buffer.from(input.toUint8Array()))
            val cwt = cborJson[0] as Cbor.Tagged
            val cwtValue = cwt.value as Array<Buffer>
            val protectedHeader = cwtValue[0]
            val unprotectedHeader = cwtValue[1].asDynamic()
            val content = cwtValue[2]
            val signature = cwtValue[3]

            val protectedHeaderCbor = Cbor.Decoder.decodeAllSync(protectedHeader)[0].asDynamic()
            val kid = protectedHeaderCbor?.get(4) as Uint8Array? ?: unprotectedHeader?.get(4) as Uint8Array

            if (kid === undefined) throw IllegalArgumentException("KID not found")

            val algorithm = protectedHeaderCbor?.get(1) ?: unprotectedHeader?.get(1)

            val pubKey = cryptoService.getCborVerificationKey(kid.toByteArray(), verificationResult)
            val result = Cose.verifySync(input, pubKey)
            verificationResult.coseVerified = true
            content.toByteArray()
        }.catch { throw it }
    }

}

suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then({ cont.resume(it) }, { cont.resumeWithException(it) })
}
