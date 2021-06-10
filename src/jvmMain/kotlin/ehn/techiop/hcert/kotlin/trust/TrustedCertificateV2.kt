package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


actual fun TrustedCertificateV2.decodeCertificate(): CertificateAdapter =
    CertificateAdapter(
        CertificateFactory.getInstance("X.509")
            .generateCertificate(certificate.inputStream()) as X509Certificate
    )
