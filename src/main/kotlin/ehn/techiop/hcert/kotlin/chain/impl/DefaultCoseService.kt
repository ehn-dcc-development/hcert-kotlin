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
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

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
                    DefaultCoseService.getKid(it)?.let { kid ->
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
            val keyFactory = when (trustedCert.keyType) {
                KeyType.RSA -> KeyFactory.getInstance("RSA")
                KeyType.EC -> KeyFactory.getInstance("EC")
            }
            val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(trustedCert.publicKey))
            return OneKey(publicKey, null)
        }
    }

}