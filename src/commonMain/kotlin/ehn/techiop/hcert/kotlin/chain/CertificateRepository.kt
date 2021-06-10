package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter

interface CertificateRepository {

    fun loadTrustedCertificates(kid: ByteArray, verificationResult: VerificationResult): List<CertificateAdapter>

}