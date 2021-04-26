package ehn.techiop.hcert.kotlin.chain

interface CertificateRepository {

    fun loadTrustedCertificates(kid: ByteArray, verificationResult: VerificationResult): List<TrustedCertificate>

}