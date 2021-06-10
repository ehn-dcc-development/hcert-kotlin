package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.EcCosePrivateKey
import ehn.techiop.hcert.kotlin.chain.toBase64UrlString
import elliptic.EC
import elliptic.EcKeyPair
import tsstdlib.JsonWebKey

class JsEcPrivKey(val dValue: Buffer, val ec: EC, private val keySizeBits: Int) : EcPrivKey<EcCosePrivateKey> {

    constructor(keyPair: EcKeyPair, keySizeBits: Int) : this(keyPair.getPrivate().toArrayLike(Buffer), keyPair.ec, keySizeBits)

    override fun toCoseRepresentation(): EcCosePrivateKey = object : EcCosePrivateKey {
        override val d: Buffer = dValue
    }

    override fun toPlatformPrivateKey(): dynamic = object : JsonWebKey {
        override var kty: String? = "EC"
        override var crv: String? = if (keySizeBits == 384) "P-384" else "P-256"
        override var d: String? = dValue.toBase64UrlString()
    }

}