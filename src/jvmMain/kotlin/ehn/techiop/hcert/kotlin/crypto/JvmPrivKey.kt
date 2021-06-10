package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.PrivateKey

class JvmPrivKey(private val privateKey: PrivateKey) : PrivKey<PrivateKey> {

    override fun toCoseRepresentation() = OneKey(null, privateKey)

    override fun toPlatformPrivateKey() = privateKey

}