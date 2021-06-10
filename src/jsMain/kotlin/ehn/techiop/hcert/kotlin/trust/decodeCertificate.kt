package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter


actual fun TrustedCertificateV2.decodeCertificate(): CertificateAdapter = CertificateAdapter(certificate)