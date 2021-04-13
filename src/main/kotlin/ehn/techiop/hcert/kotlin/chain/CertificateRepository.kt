package ehn.techiop.hcert.kotlin.chain

import java.security.cert.Certificate

interface CertificateRepository {

    fun loadCertificate(kid: String): Certificate

}