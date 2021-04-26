package ehn.techiop.hcert.kotlin.chain.impl

import COSE.HeaderKeys
import COSE.KeyKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.KeyType
import ehn.techiop.hcert.kotlin.chain.TrustedCertificate
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec

class VerificationCoseService(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                try {
                    getKid(it)?.let { kid ->
                        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
                            verificationResult.certificateValidFrom = trustedCert.validFrom
                            verificationResult.certificateValidUntil = trustedCert.validUntil
                            verificationResult.certificateValidContent = trustedCert.certType
                            val buildOneKey = buildOneKey(trustedCert)
                            if (it.validate(buildOneKey)) {
                                verificationResult.coseVerified = true
                                return it.GetContent()
                            }
                        }
                    }
                } catch (e: Throwable) {
                    it.GetContent()
                }
            }.GetContent()
        } catch (e: Throwable) {
            input
        }
    }

    private fun buildOneKey(trustedCert: TrustedCertificate): OneKey {
        return when (val publicKey = buildPublicKey(trustedCert)) {
            is RSAPublicKey -> buildRsaKey(publicKey)
            is ECPublicKey -> buildEcKey(publicKey)
            else -> throw IllegalArgumentException("keyType")
        }
    }

    private fun buildPublicKey(trustedCert: TrustedCertificate) = when (trustedCert.keyType) {
        KeyType.RSA -> KeyFactory.getInstance("RSA")
        KeyType.EC -> KeyFactory.getInstance("EC")
    }.generatePublic(X509EncodedKeySpec(trustedCert.publicKey))

    private fun buildRsaKey(rsaPublicKey: RSAPublicKey): OneKey {
        return OneKey(CBORObject.NewMap().also {
            it[KeyKeys.KeyType.AsCBOR()] = KeyKeys.KeyType_RSA
            it[KeyKeys.RSA_N.AsCBOR()] = stripLeadingZero(rsaPublicKey.modulus)
            it[KeyKeys.RSA_E.AsCBOR()] = stripLeadingZero(rsaPublicKey.publicExponent)
        })
    }

    private fun buildEcKey(publicKey: ECPublicKey): OneKey {
        return OneKey(CBORObject.NewMap().also {
            it[KeyKeys.KeyType.AsCBOR()] = KeyKeys.KeyType_EC2
            it[KeyKeys.EC2_Curve.AsCBOR()] = getEcCurve(publicKey)
            it[KeyKeys.EC2_X.AsCBOR()] = stripLeadingZero(publicKey.w.affineX)
            it[KeyKeys.EC2_Y.AsCBOR()] = stripLeadingZero(publicKey.w.affineY)
        })
    }

    private fun getEcCurve(publicKey: ECPublicKey) = when (publicKey.params.order.bitLength()) {
        384 -> KeyKeys.EC2_P384
        256 -> KeyKeys.EC2_P256
        else -> throw IllegalArgumentException("curveSize")
    }

    /**
     * Strip the possibly leading zero (used as the sign bit) added from Java's BigInteger implementation
     */
    private fun stripLeadingZero(input: BigInteger): CBORObject {
        val bytes = input.toByteArray()
        val stripped = when {
            bytes.size % 8 != 0 && bytes[0] == 0x00.toByte() -> bytes.drop(1).toByteArray()
            else -> bytes
        }
        return CBORObject.FromObject(stripped)
    }

    companion object {
        fun getKid(it: Sign1Message): ByteArray? {
            val key = HeaderKeys.KID.AsCBOR()
            if (it.protectedAttributes.ContainsKey(key)) {
                return it.protectedAttributes.get(key).GetByteString()
            } else if (it.unprotectedAttributes.ContainsKey(key)) {
                return it.unprotectedAttributes.get(key).GetByteString()
            }
            return null
        }
    }

}