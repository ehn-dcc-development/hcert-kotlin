package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.EcCosePrivateKey
import elliptic.EC
import elliptic.EcKeyPair

class JsEcPrivKey(val dValue: Buffer, val ec: EC) : EcPrivKey<EcCosePrivateKey> {

    constructor(keyPair: EcKeyPair) : this(keyPair.getPrivate().toArrayLike(Buffer), keyPair.ec)

    override fun toCoseRepresentation(): EcCosePrivateKey = object : EcCosePrivateKey {
        override val d: Buffer = dValue
    }
}