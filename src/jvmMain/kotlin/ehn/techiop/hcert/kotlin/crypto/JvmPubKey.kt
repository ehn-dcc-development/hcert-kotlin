package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.PublicKey

class JvmPubKey(private val publicKey: PublicKey) : PubKey {

    fun toCoseRepresentation() = OneKey(publicKey, null)

    fun toPlatformPublicKey() = publicKey

}