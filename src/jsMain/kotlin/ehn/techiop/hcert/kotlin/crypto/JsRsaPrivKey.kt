package ehn.techiop.hcert.kotlin.crypto

import Buffer
import cose.RsaCosePrivateKey
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
}