package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.JsCertificate

actual fun TrustedCertificateV2.decodeCertificate(): Certificate<*> = JsCertificate(certificate)