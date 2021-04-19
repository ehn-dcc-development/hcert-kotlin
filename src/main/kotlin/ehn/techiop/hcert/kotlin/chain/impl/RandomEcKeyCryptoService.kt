package ehn.techiop.hcert.kotlin.chain.impl

import COSE.AlgorithmID
import COSE.HeaderKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import org.bouncycastle.asn1.x500.X500Name
import java.security.KeyPairGenerator
import java.security.cert.Certificate
import java.security.cert.X509Certificate

class RandomEcKeyCryptoService(private val keySize: Int = 256) : CryptoService {

    private val pkiUtils = PkiUtils()

    private val keyPair = KeyPairGenerator.getInstance("EC")
        .apply { initialize(keySize) }.genKeyPair()

    private val keyPairCert: X509Certificate = pkiUtils.selfSignCertificate(X500Name("CN=EC-Me"), keyPair)

    private val keyId = pkiUtils.calcKid(keyPairCert)

    override fun getCborHeaders() = listOf(
        Pair(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR()),
        Pair(HeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = OneKey(keyPair.public, keyPair.private)

    override fun getCborVerificationKey(kid: ByteArray): OneKey {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        return OneKey(keyPair.public, keyPair.private)
    }

    override fun getCertificate(kid: ByteArray): Certificate {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        return keyPairCert
    }

}


