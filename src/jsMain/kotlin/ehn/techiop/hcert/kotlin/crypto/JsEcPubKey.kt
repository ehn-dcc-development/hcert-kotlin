package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.EcCosePublicKey
import elliptic.EcKeyPair
import elliptic.EcPublicKey
import org.khronos.webgl.Uint8Array

class JsEcPubKey(val xCoord: Buffer, val yCoord: Buffer) : EcPubKey<dynamic> {

    constructor(ecPublicKey: EcPublicKey) : this(
        ecPublicKey.getX().toArrayLike(Buffer),
        ecPublicKey.getY().toArrayLike(Buffer),
    )

    constructor(ecKeyPair: EcKeyPair) : this(ecKeyPair.getPublic())

    constructor(x: Uint8Array, y: Uint8Array) : this(
        xCoord = Buffer.from(x),
        yCoord = Buffer.from(y),
    )

    override fun toCoseRepresentation(): EcCosePublicKey {
        return object : EcCosePublicKey {
            override val x = xCoord
            override val y = yCoord
        }
    }

}