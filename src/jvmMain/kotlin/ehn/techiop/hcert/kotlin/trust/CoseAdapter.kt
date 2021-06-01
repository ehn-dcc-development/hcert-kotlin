package ehn.techiop.hcert.kotlin.trust

import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

actual class CoseAdapter actual constructor(private val input: ByteArray) {

    val sign1Message = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message

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
            if (sign1Message.validate(it.cosePublicKey.toCoseRepresentation() as OneKey)) {
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
            verificationResult.certificateValidFrom = trustedCert.validFrom
            verificationResult.certificateValidUntil = trustedCert.validUntil
            verificationResult.certificateValidContent = trustedCert.validContentTypes
            if (sign1Message.validate(trustedCert.cosePublicKey.toCoseRepresentation() as OneKey)) {
                verificationResult.coseVerified = true
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
        verificationResult.coseVerified = sign1Message.validate(verificationKey.toCoseRepresentation() as OneKey)
        val trustedCert = cryptoService.getCertificate()
        verificationResult.certificateValidFrom = trustedCert.validFrom
        verificationResult.certificateValidUntil = trustedCert.validUntil
        verificationResult.certificateValidContent = trustedCert.validContentTypes
        return verificationResult.coseVerified
    }

    actual fun getContent() = sign1Message.GetContent()

    actual fun getContentMap() = CwtAdapter(sign1Message.GetContent())

}