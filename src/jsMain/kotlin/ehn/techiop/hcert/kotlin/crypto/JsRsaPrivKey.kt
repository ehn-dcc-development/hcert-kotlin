package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.RsaCosePrivateKey
import ehn.techiop.hcert.kotlin.chain.toBase64UrlString
import org.khronos.webgl.Int32Array
import tsstdlib.JsonWebKey
import kotlin.js.Json

class JsRsaPrivKey(val raw: Json) : RsaPrivKey<dynamic> {

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