package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey

class FileBasedCryptoService constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) :
    CryptoService {

    private val cryptoAdapter = LoadedCryptoAdapter(pemEncodedPrivateKey, pemEncodedCertificate)

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, cryptoAdapter.algorithm),
        Pair(CoseHeaderKeys.KID, cryptoAdapter.certificate.kid)
    )

    override fun getCborSigningKey() = cryptoAdapter.privateKey

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): PubKey {
        if (!(cryptoAdapter.certificate.kid contentEquals kid))
            throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "kid not known: $kid")

        verificationResult.setCertificateData(cryptoAdapter.certificate)
        return cryptoAdapter.certificate.publicKey
    }

    override fun getCertificate(): CertificateAdapter = cryptoAdapter.certificate

    override fun exportPrivateKeyAsPem() = "-----BEGIN PRIVATE KEY-----\n" +
            base64forPem(cryptoAdapter.exportPrivateKey) +
            "\n-----END PRIVATE KEY-----\n"

    override fun exportCertificateAsPem() = "-----BEGIN CERTIFICATE-----\n" +
            base64forPem(cryptoAdapter.exportCertificate) +
            "\n-----END CERTIFICATE-----\n"

    private fun base64forPem(encoded: ByteArray) =
        encoded.asBase64().chunked(64).joinToString(separator = "\n")

}


expect class LoadedCryptoAdapter constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) {

    val privateKey: PrivKey
    val algorithm: CwtAlgorithm
    val certificate: CertificateAdapter
    val exportPrivateKey: ByteArray
    val exportCertificate: ByteArray

}