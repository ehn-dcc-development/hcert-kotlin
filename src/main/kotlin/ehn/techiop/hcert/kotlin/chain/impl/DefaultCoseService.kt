package ehn.techiop.hcert.kotlin.chain.impl

import COSE.Attribute
import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.KeyType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import org.bouncycastle.asn1.pkcs.RSAPublicKey
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.security.KeyFactory
import java.security.spec.ECPublicKeySpec
import java.security.spec.RSAPublicKeySpec


open class DefaultCoseService(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            cryptoService.getCborHeaders().forEach { header ->
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                try {
                    getKid(it)?.let { kid ->
                        val verificationKey = cryptoService.getCborVerificationKey(kid, verificationResult)
                        verificationResult.coseVerified = it.validate(verificationKey)
                    }
                } catch (e: Throwable) {
                    it.GetContent()
                }
            }.GetContent()
        } catch (e: Throwable) {
            input
        }
    }

    companion object {
        internal fun getKid(it: Sign1Message): ByteArray? {
            val key = HeaderKeys.KID.AsCBOR()
            if (it.protectedAttributes.ContainsKey(key)) {
                return it.protectedAttributes.get(key).GetByteString()
            } else if (it.unprotectedAttributes.ContainsKey(key)) {
                return it.unprotectedAttributes.get(key).GetByteString()
            }
            return null
        }

        internal fun buildOneKey(trustedCert: TrustedCertificate): OneKey {
            val publicKey = when (trustedCert.keyType) {
                KeyType.RSA -> {
                    val rsaPublicKey = RSAPublicKey.getInstance(trustedCert.publicKey)
                    KeyFactory.getInstance("RSA")
                        .generatePublic(RSAPublicKeySpec(rsaPublicKey.modulus, rsaPublicKey.publicExponent))
                }
                KeyType.EC -> {
                    val name = getEcCurveName(trustedCert)
                    val spec = ECNamedCurveTable.getParameterSpec(name)
                    val params = ECNamedCurveSpec(name, spec.curve, spec.g, spec.n)
                    val publicPoint = ECPointUtil.decodePoint(params.curve, trustedCert.publicKey)
                    KeyFactory.getInstance("EC").generatePublic(ECPublicKeySpec(publicPoint, params))
                }
            }
            return OneKey(publicKey, null)
        }

        private fun getEcCurveName(trustedCert: TrustedCertificate) =
            when (trustedCert.publicKey.size) {
                65 -> "secp256r1"
                97 -> "secp384r1"
                else -> throw IllegalArgumentException("key")
            }
    }

}