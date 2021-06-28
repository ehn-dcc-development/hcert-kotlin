package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CryptoAdapter
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

class RandomRsaKeyCryptoService constructor(
    keySize: Int = 2048,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : CryptoService {

    private val cryptoAdapter = CryptoAdapter(KeyType.RSA, keySize, contentType, clock)

    private val keyId = cryptoAdapter.certificate.kid

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, cryptoAdapter.algorithm),
        Pair(CoseHeaderKeys.KID, keyId)
    )

    override fun getCborSigningKey() = cryptoAdapter.privateKey

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): PubKey {
        if (!(keyId contentEquals kid))
            throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "kid not known: $kid")

        verificationResult.setCertificateData(cryptoAdapter.certificate)
        return cryptoAdapter.certificate.publicKey
    }

    override fun getCertificate(): CertificateAdapter = cryptoAdapter.certificate

    override fun exportPrivateKeyAsPem() = "-----BEGIN PRIVATE KEY-----\n" +
            base64forPem(cryptoAdapter.privateKeyEncoded) +
            "\n-----END PRIVATE KEY-----\n"

    override fun exportCertificateAsPem() = "-----BEGIN CERTIFICATE-----\n" +
            base64forPem(cryptoAdapter.certificate.encoded) +
            "\n-----END CERTIFICATE-----\n"

    private fun base64forPem(encoded: ByteArray) =
        encoded.asBase64().chunked(64).joinToString(separator = "\n")

}
