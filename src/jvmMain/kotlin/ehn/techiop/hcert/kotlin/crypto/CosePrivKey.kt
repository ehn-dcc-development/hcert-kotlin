package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey

class CosePrivKey(val oneKey: OneKey) : PrivKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}