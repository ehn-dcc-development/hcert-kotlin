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

class RandomRsaKeyCryptoService constructor(
    keySize: Int = 2048,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : CryptoService {

    private val cryptoAdapter = CryptoAdapter(KeyType.RSA, keySize)
    private val algorithm = CwtAlgorithm.RSA_PSS_256
    private val certificate = PkiUtils().selfSignCertificate(
        "RSA-Me",
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
