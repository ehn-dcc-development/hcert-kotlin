package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.RsaCosePrivateKey
import ehn.techiop.hcert.kotlin.chain.toBase64UrlString
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toHexString
import org.khronos.webgl.Int32Array
import pkijs.src.RSAPrivateKey.RSAPrivateKey
import tsstdlib.JsonWebKey
import kotlin.js.Json
import kotlin.js.json

class JsRsaPrivKey(val raw: Json) : PrivKey<dynamic> {

    constructor(privateKey: RSAPrivateKey) : this(
        json(
            "n" to privateKey.modulus.valueBlock.valueHex.toByteArray().toBuffer(),
            "e" to privateKey.publicExponent.valueBlock.valueHex.toByteArray().toHexString().toInt(16) as Number,
            "d" to privateKey.privateExponent.valueBlock.valueHex.toByteArray().toBuffer(),
            "p" to privateKey.prime1.valueBlock.valueHex.toByteArray().toBuffer(),
            "q" to privateKey.prime2.valueBlock.valueHex.toByteArray().toBuffer(),
            "dmp1" to privateKey.exponent1.valueBlock.valueHex.toByteArray().toBuffer(),
            "dmq1" to privateKey.exponent2.valueBlock.valueHex.toByteArray().toBuffer(),
            "coeff" to privateKey.coefficient.valueBlock.valueHex.toByteArray().toBuffer(),
        )
    )

    override fun toCoseRepresentation(): RsaCosePrivateKey = object : RsaCosePrivateKey {
        override val p: Buffer = raw["p"] as Buffer
        override val q: Buffer = raw["q"] as Buffer
        override val dp: Buffer = raw["dmp1"] as Buffer
        override val dq: Buffer = raw["dmq1"] as Buffer
        override val qi: Buffer = raw["coeff"] as Buffer
        override val d: Buffer = raw["d"] as Buffer
        override val n: Buffer = raw["n"] as Buffer
        override val e: Number = raw["e"] as Number
    }

    override fun toPlatformPrivateKey() = object : JsonWebKey {
        override var alg: String? = "PS256"
        override var kty: String? = "RSA"
        override var p: String? = (raw["p"] as Buffer).toBase64UrlString()
        override var q: String? = (raw["q"] as Buffer).toBase64UrlString()
        override var dp: String? = (raw["dmp1"] as Buffer).toBase64UrlString()
        override var dq: String? = (raw["dmq1"] as Buffer).toBase64UrlString()
        override var qi: String? = (raw["coeff"] as Buffer).toBase64UrlString()
        override var d: String? = (raw["d"] as Buffer).toBase64UrlString()
        override var n: String? = (raw["n"] as Buffer).toBase64UrlString()
        override var e: String? = Buffer(Int32Array(arrayOf((raw["e"] as Number).toInt())).buffer).toBase64UrlString()
    }

}