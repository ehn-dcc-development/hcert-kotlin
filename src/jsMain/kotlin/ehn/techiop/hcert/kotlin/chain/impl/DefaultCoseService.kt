package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Cbor
import ehn.techiop.hcert.kotlin.crypto.Cose
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.suspendCoroutine
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
        val cborJson = Cbor.decode(input)
        val protectedHeader = cborJson["value"][0]
        val unprotectedHeader = cborJson["value"][1]
        val content = cborJson["value"][2]
        val signature = cborJson["value"][3]
        val protectedHeaderCbor = Cbor.decode(protectedHeader)
        val kid = protectedHeaderCbor.get(4) as Uint8Array? ?: if (unprotectedHeader.length !== undefined)
            Cbor.decode(unprotectedHeader).get(1) as Uint8Array
        else
            throw IllegalArgumentException("KID not found")
        if (kid === undefined)
            throw IllegalArgumentException("KID not found")
        val algorithm = protectedHeaderCbor.get(1)
        val pubKey = cryptoService.getCborVerificationKey(kid.toByteArray(), verificationResult)
        Cose.verify(input, pubKey).also {
            it.then { verificationResult.coseVerified = true }
        }
        return content
    }

    actual companion object {
        actual fun getInstance(cryptoService: CryptoService): DefaultCoseService {
            return DefaultCoseService(cryptoService)
        }
    }
}
