package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.PrivateKey

open class JvmPrivKey(private val privateKey: PrivateKey) : PrivKey {

    open fun toCoseRepresentation() = OneKey(null, privateKey)

    open fun toPlatformPrivateKey() = privateKey

}