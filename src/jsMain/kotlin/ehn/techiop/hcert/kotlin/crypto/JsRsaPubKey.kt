package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.RsaCosePublicKey
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

class JsRsaPubKey(val modulus: ArrayBuffer, val publicExponent: Number) :
    PubKey<dynamic> {

    override fun toCoseRepresentation(): RsaCosePublicKey = object : RsaCosePublicKey {
        override val n = Buffer.from(Uint8Array(modulus))
        override val e = publicExponent
    }
}