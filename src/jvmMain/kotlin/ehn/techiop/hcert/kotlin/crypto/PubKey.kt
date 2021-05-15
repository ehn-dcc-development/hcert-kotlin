package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import ehn.techiop.hcert.kotlin.trust.KeyType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.asn1.sec.SECObjectIdentifiers
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.security.KeyFactory
import java.security.interfaces.ECPublicKey
import java.security.spec.ECPublicKeySpec
import java.security.spec.RSAPublicKeySpec

class CosePubKey(val oneKey: OneKey) : PublicKey<OneKey> {
    override fun toCoseRepresenation() = oneKey
}

