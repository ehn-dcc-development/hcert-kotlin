package ehn.techiop.hcert.kotlin.crypto

import java.security.PublicKey

class JvmPubKey(val publicKey: PublicKey) : PubKey<PublicKey> {
    override fun toCoseRepresentation() = publicKey
}