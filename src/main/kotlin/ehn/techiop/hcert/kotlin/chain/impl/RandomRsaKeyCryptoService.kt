package ehn.techiop.hcert.kotlin.chain.impl

import COSE.AlgorithmID
import COSE.HeaderKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPairGenerator
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.X509Certificate

class RandomRsaKeyCryptoService(private val keySize: Int = 2048) : CryptoService {

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    private val pkiUtils = PkiUtils()

    private val keyPair = KeyPairGenerator.getInstance("RSA")
        .apply { initialize(keySize) }.genKeyPair()

    private val keyPairCert = pkiUtils.selfSignCertificate(X500Name("CN=RSA-Me"), keyPair)

    private val keyId = pkiUtils.calcKid(keyPairCert)

    override fun getCborHeaders() = listOf(
        Pair(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_256.AsCBOR()),
        Pair(HeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = OneKey(keyPair.public, keyPair.private)

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): OneKey {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        verificationResult.certificateValidFrom = pkiUtils.getValidFrom(keyPairCert)
        verificationResult.certificateValidUntil = pkiUtils.getValidUntil(keyPairCert)
        return OneKey(keyPair.public, keyPair.private)
    }

    override fun getCertificate(): Pair<ByteArray, X509Certificate> {
        return Pair(keyId, keyPairCert)
    }


}


