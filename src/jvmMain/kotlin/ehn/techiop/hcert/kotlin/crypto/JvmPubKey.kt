package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.PublicKey

open class JvmPubKey(private val publicKey: PublicKey) : PubKey {

    open fun toCoseRepresentation() = OneKey(publicKey, null)

    open fun toPlatformPublicKey() = publicKey

}