package ehn.techiop.hcert.kotlin.trust

import COSE.MessageTag
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.crypto.JvmPubKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

actual class CoseAdapter actual constructor(private val input: ByteArray) {

    val sign1Message = try {
        Sign1Message.DecodeFromBytes(augmentedInput(input), MessageTag.Sign1) as Sign1Message
    } catch (t: Throwable) {
        throw VerificationException(Error.SIGNATURE_INVALID, cause = t)
    }

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    actual fun getProtectedAttributeByteArray(key: Int) =
        sign1Message.protectedAttributes[key]?.GetByteString()

    actual fun getUnprotectedAttributeByteArray(key: Int) =
        sign1Message.unprotectedAttributes[key]?.GetByteString()

    actual fun getProtectedAttributeInt(key: Int) =
        sign1Message.protectedAttributes[key]?.AsInt32()

    actual fun validate(kid: ByteArray, repository: CertificateRepository): Boolean {
        repository.loadTrustedCertificates(kid, VerificationResult()).forEach {
            if (sign1Message.validate((it.publicKey as JvmPubKey).toCoseRepresentation())) {
                return true
            }
        }
        return false
    }

    actual fun validate(
        kid: ByteArray,
        repository: CertificateRepository,
        verificationResult: VerificationResult
    ): Boolean {
        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
            verificationResult.setCertificateData(trustedCert)
            if (sign1Message.validate((trustedCert.publicKey as JvmPubKey).toCoseRepresentation())) {
                return true
            }
        }
        return false
    }

    actual fun validate(
        kid: ByteArray,
        cryptoService: CryptoService,
        verificationResult: VerificationResult
    ): Boolean {
        val verificationKey = cryptoService.getCborVerificationKey(kid, verificationResult)
        verificationResult.setCertificateData(cryptoService.getCertificate())
        return sign1Message.validate((verificationKey as JvmPubKey).toCoseRepresentation())
    }

    actual fun getContent() = sign1Message.GetContent()

    actual fun getContentMap(): CwtAdapter = JvmCwtAdapter(sign1Message.GetContent())

    /**
     * Input may be missing COSE Tag 0xD2 = 18 = cose-sign1.
     * But we need this, to cast the parsed object to [Cbor.Tagged].
     * So we'll add the Tag to the input.
     *
     * It may also be tagged as a CWT (0xD8, 0x3D) and a Sign1 (0xD2).
     * But the library expects only one tag.
     * So we'll strip the CWT tag from the input.
     */
    private fun augmentedInput(input: ByteArray): ByteArray {
        if (input.size >= 1 && isArray(input[0]))
            return byteArrayOf(0xD2.toByte()) + input
        if (input.size >= 3 && isCwt(input[0], input[1]) && isSign1(input[2])) {
            return input.drop(2).toByteArray()
        }
        return input
    }

    private fun isSign1(byte: Byte) = byte == 0xD2.toByte()

    private fun isCwt(firstByte: Byte, secondByte: Byte) = firstByte == 0xD8.toByte() && secondByte == 0x3D.toByte()

    private fun isArray(byte: Byte) = byte == 0x84.toByte()

}