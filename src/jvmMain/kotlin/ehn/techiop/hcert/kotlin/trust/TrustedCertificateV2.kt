package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import ehn.techiop.hcert.kotlin.crypto.PublicKey
import kotlinx.datetime.Instant
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


actual fun TrustedCertificateV2.decodeCertificate(): ehn.techiop.hcert.kotlin.crypto.Certificate<*> =
    JvmCertificate(
        CertificateFactory.getInstance("X.509").generateCertificate(certificate.inputStream()) as X509Certificate
    )
