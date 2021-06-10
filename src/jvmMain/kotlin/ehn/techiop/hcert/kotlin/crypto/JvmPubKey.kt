package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.PublicKey

class JvmPubKey(private val publicKey: PublicKey) : PubKey<PublicKey> {

    override fun toCoseRepresentation() = OneKey(publicKey, null)

    override fun toPlatformPublicKey() = publicKey

}