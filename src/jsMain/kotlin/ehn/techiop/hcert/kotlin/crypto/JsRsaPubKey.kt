package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.RsaCosePublicKey
import ehn.techiop.hcert.kotlin.chain.toBase64UrlString
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Uint8Array
import tsstdlib.JsonWebKey

class JsRsaPubKey(val modulus: ArrayBuffer, val publicExponent: Number) :
    PubKey<dynamic> {

    override fun toCoseRepresentation(): RsaCosePublicKey = object : RsaCosePublicKey {
        override val n = Buffer.from(Uint8Array(modulus))
        override val e = publicExponent
    }

    override fun toPlatformPublicKey() = object : JsonWebKey {
        override var alg: String? = "RS256"
        override var kty: String? = "RSA"
        override var n: String? = stripLeadingZero(Buffer.from(Uint8Array(modulus))).toBase64UrlString()
        override var e: String? = Buffer(Int32Array(arrayOf(publicExponent.toInt())).buffer).toBase64UrlString()
    }

    // We'll need to strip the leading zero from the Buffer
    // because ASN.1 will add it's own leading zero, if needed
    private fun stripLeadingZero(n: Buffer): Buffer {
        return if (n.readUInt8(0) == 0) n.slice(1) else n
    }
}