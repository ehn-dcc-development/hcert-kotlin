package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey

class CosePubKey(val oneKey: OneKey) : PubKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}