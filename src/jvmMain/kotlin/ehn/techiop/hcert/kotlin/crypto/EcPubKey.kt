package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import java.security.interfaces.ECPublicKey

class CoseEcPubKey(val ecPubKey: ECPublicKey) : EcPubKey<OneKey> {
    override fun toCoseRepresenation() = OneKey(ecPubKey, null)
    override val curve: CurveIdentifier
        get() = TODO("Not yet implemented")
}