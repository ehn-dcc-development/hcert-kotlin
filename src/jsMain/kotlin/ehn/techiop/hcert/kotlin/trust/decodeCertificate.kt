package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.JsCertificate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi

actual fun TrustedCertificateV2.decodeCertificate(): Certificate<*> = JsCertificate(certificate)