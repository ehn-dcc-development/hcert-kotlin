package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.JsCertificate


actual fun TrustedCertificateV2.decodeCertificate(): CertificateAdapter<*> = JsCertificate(certificate)