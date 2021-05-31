package ehn.techiop.hcert.kotlin.chain.impl

import COSE.Attribute
import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

actual open class DefaultCoseService actual constructor(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            cryptoService.getCborHeaders().forEach { header ->
                it.addAttribute(
                    CBORObject.FromObject(header.first.value),
                    CBORObject.FromObject(header.second),
                    Attribute.PROTECTED
                )
            }
            it.sign(cryptoService.getCborSigningKey().toCoseRepresentation() as OneKey)
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        val msg = Sign1Message.DecodeFromBytes(strippedInput(input), MessageTag.Sign1) as Sign1Message
        val kid = msg.findAttribute(HeaderKeys.KID)?.GetByteString() ?: throw IllegalArgumentException("kid")
        val verificationKey = cryptoService.getCborVerificationKey(kid, verificationResult)
        verificationResult.coseVerified = msg.validate(verificationKey.toCoseRepresentation() as OneKey)
        val trustedCert = cryptoService.getCertificate()
        verificationResult.certificateValidFrom = trustedCert.validFrom
        verificationResult.certificateValidUntil = trustedCert.validUntil
        verificationResult.certificateValidContent = trustedCert.validContentTypes
        return msg.GetContent()
    }


    // Input may be tagged as a CWT and a Sign1
    private fun strippedInput(input: ByteArray): ByteArray {
        if (input.size >= 3 && input[0] == 0xD8.toByte() && input[1] == 0x3D.toByte() && input[2] == 0xD2.toByte())
            return input.drop(2).toByteArray()
        return input
    }

}