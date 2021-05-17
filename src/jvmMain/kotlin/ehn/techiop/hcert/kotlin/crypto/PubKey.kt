package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.cert.X509Certificate

class CosePubKey(val oneKey: OneKey) : PublicKey<OneKey> {
    override fun toCoseRepresenation() = oneKey
}

class CosePrivateKey(val oneKey: OneKey) : PrivateKey<OneKey> {
    override fun toCoseRepresenation() = oneKey
}

class JvmCertificate(val certificate: X509Certificate) : Certificate<X509Certificate> {

}
