package ehn.techiop.hcert.kotlin.trust

import COSE.OneKey
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.asn1.sec.SECObjectIdentifiers
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.security.KeyFactory
import java.security.spec.ECPublicKeySpec
import java.security.spec.RSAPublicKeySpec

actual fun TrustedCertificate.buildOneKey(): ByteArray = oneKey.EncodeToBytes()

val TrustedCertificate.oneKey: OneKey
    get() {
        val publicKey = when (keyType) {
            KeyType.RSA -> {
                val rsaPublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(publicKey)
                val spec = RSAPublicKeySpec(rsaPublicKey.modulus, rsaPublicKey.publicExponent)
                KeyFactory.getInstance("RSA").generatePublic(spec)
            }
            KeyType.EC -> {
                val ecCurveName = when (publicKey.size) {
                    65 -> SECNamedCurves.getName(SECObjectIdentifiers.secp256r1)
                    97 -> SECNamedCurves.getName(SECObjectIdentifiers.secp384r1)
                    else -> throw IllegalArgumentException("key")
                }
                val param = SECNamedCurves.getByName(ecCurveName)
                val paramSpec = ECNamedCurveSpec(ecCurveName, param.curve, param.g, param.n)
                val publicPoint = ECPointUtil.decodePoint(paramSpec.curve, publicKey)
                val spec = ECPublicKeySpec(publicPoint, paramSpec)
                KeyFactory.getInstance("EC").generatePublic(spec)
            }
        }
        return OneKey(publicKey, null)
    }