package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import kotlinx.serialization.ExperimentalSerializationApi
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.time.ExperimentalTime


@ExperimentalSerializationApi

actual fun TrustedCertificateV2.decodeCertificate(): ehn.techiop.hcert.kotlin.crypto.Certificate<*> =
    JvmCertificate(
        CertificateFactory.getInstance("X.509")
            .generateCertificate(certificate.inputStream()) as X509Certificate
    )
