package ehn.techiop.hcert.kotlin.chain.impl

import COSE.HeaderKeys
import COSE.KeyKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.math.BigInteger
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey

class VerificationCryptoService(private val repository: CertificateRepository) : CryptoService {

    override fun getCborHeaders() = listOf<Pair<HeaderKeys, CBORObject>>()

    override fun getCborSigningKey() = OneKey(CBORObject.NewMap())

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): OneKey {
        return when (val publicKey = repository.loadPublicKey(kid, verificationResult)) {
            is ECPublicKey -> buildEcKey(publicKey)
            is RSAPublicKey -> buildRsaKey(publicKey)
            else -> throw IllegalArgumentException("Key type not known")
        }
    }

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
        521 -> KeyKeys.EC2_P521
        else -> KeyKeys.EC2_P256
    }

    override fun getCertificate(): Pair<ByteArray, X509Certificate> {
        throw NotImplementedError()
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

}


