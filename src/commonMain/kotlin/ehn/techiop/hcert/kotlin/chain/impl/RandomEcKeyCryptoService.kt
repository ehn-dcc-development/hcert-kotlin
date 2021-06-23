package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CryptoAdapter
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

class RandomEcKeyCryptoService constructor(
    keySize: Int = 256,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : CryptoService {

    private val cryptoAdapter = CryptoAdapter(KeyType.EC, keySize)
    private val algorithm = when (keySize) {
        256 -> CwtAlgorithm.ECDSA_256
        384 -> CwtAlgorithm.ECDSA_384
        else -> throw IllegalArgumentException("keySize: $keySize")
    }
    private val certificate = PkiUtils.selfSignCertificate(
        "EC-Me",
        cryptoAdapter.privateKey,
        cryptoAdapter.publicKey,
        keySize,
        contentType,
        clock
    )
    private val keyId = certificate.kid

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, algorithm),
        Pair(CoseHeaderKeys.KID, keyId)
    )

    override fun getCborSigningKey() = cryptoAdapter.privateKey

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): PubKey {
        if (!(keyId contentEquals kid))
            throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "kid not known: $kid")

        verificationResult.setCertificateData(certificate)
        return cryptoAdapter.publicKey
    }

    override fun getCertificate(): CertificateAdapter = certificate

    override fun exportPrivateKeyAsPem() = "-----BEGIN PRIVATE KEY-----\n" +
            cryptoAdapter.privateKeyBase64 +
            "\n-----END PRIVATE KEY-----\n"

    override fun exportCertificateAsPem() = "-----BEGIN CERTIFICATE-----\n" +
            base64forPem(certificate.encoded) +
            "\n-----END CERTIFICATE-----\n"

    private fun base64forPem(encoded: ByteArray) =
        encoded.asBase64().chunked(64).joinToString(separator = "\n")

}
