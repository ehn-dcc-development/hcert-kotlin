package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.EcCosePublicKey
import ehn.techiop.hcert.kotlin.chain.toBase64UrlString
import elliptic.EcKeyPair
import elliptic.EcPublicKey
import org.khronos.webgl.Uint8Array
import tsstdlib.JsonWebKey

class JsEcPubKey(private val xCoord: Buffer, private val yCoord: Buffer, private val keySizeBits: Int) :
    JsPubKey {

    constructor(ecPublicKey: EcPublicKey, keySize: Int) : this(
        ecPublicKey.getX().toArrayLike(Buffer),
        ecPublicKey.getY().toArrayLike(Buffer),
        keySize
    )

    constructor(ecKeyPair: EcKeyPair, keySize: Int) : this(ecKeyPair.getPublic(), keySize)

    constructor(x: Uint8Array, y: Uint8Array) : this(
        xCoord = Buffer.from(x),
        yCoord = Buffer.from(y),
        if (x.length == 48) 384 else 256
    )

    override fun toCoseRepresentation(): EcCosePublicKey {
        return object : EcCosePublicKey {
            override val x = xCoord
            override val y = yCoord
        }
    }

    override fun toPlatformPublicKey() = object : JsonWebKey {
        override var alg: String? = "EC"
        override var crv: String? = if (keySizeBits == 384) "P-384" else "P-256"
        override var kty: String? = "EC"
        override var x: String? = xCoord.toBase64UrlString()
        override var y: String? = yCoord.toBase64UrlString()
    }

}