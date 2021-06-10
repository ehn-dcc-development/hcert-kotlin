package ehn.techiop.hcert.kotlin.crypto

import java.security.PrivateKey

class JvmPrivKey(val privateKey: PrivateKey) : PrivKey<PrivateKey> {
    override fun toCoseRepresentation() = privateKey
}