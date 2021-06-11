package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.PrivateKey

class JvmPrivKey(private val privateKey: PrivateKey) : PrivKey {

    fun toCoseRepresentation() = OneKey(null, privateKey)
    fun toPlatformPrivateKey() = privateKey

}