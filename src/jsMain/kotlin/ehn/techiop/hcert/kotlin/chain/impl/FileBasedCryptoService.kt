package ehn.techiop.hcert.kotlin.chain.impl

import Asn1js.Sequence
import Asn1js.fromBER
import Buffer
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.JsCertificate
import ehn.techiop.hcert.kotlin.crypto.JsEcPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsRsaPrivKey
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import elliptic.EC
import org.khronos.webgl.Uint8Array
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.ECPrivateKey.ECPrivateKey
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo
import pkijs.src.RSAPrivateKey.RSAPrivateKey

actual class FileBasedCryptoService actual constructor(pemEncodedKeyPair: String, pemEncodedCertificate: String) :
    CryptoService {

    private val privateKeyInfo: PrivateKeyInfo
    private val privateKey: PrivKey<*>
    private val publicKey: PubKey<*>
    private val algorithmID: CwtAlgorithm
    private val certificate: JsCertificate
    private val keyId: ByteArray

    init {
        val array = cleanPem(pemEncodedKeyPair).fromBase64().toTypedArray()
        privateKeyInfo = Uint8Array(array).let { bytes ->
            fromBER(bytes.buffer).result.let {
                PrivateKeyInfo(js("({'schema':it})"))
            }
        }
        val oid = (privateKeyInfo.privateKeyAlgorithm as AlgorithmIdentifier).algorithmId
        if (oid == "1.2.840.10045.2.1") {
            val buffer = Buffer(privateKeyInfo.privateKey.valueBlock.valueHex)
            val ecPrivateKey = fromBER(buffer.buffer).result.let {
                ECPrivateKey(js("({'schema':it})"))
            }
            val content = Buffer(ecPrivateKey.privateKey.valueBlock.valueHex)
            privateKey = JsEcPrivKey(EC("p256").keyFromPrivate(content))
            algorithmID = CwtAlgorithm.ECDSA_256
        } else if (oid == "1.2.840.113549.1.1.1") {
            // TODO untested
            val buffer = Buffer(privateKeyInfo.privateKey.valueBlock.valueHex)
            val rsaPrivateKey = fromBER(buffer.buffer).result.let {
                RSAPrivateKey(js("({'schema':it})"))
            }
            privateKey = JsRsaPrivKey(buffer.buffer)
            algorithmID = CwtAlgorithm.RSA_PSS_256
        } else throw IllegalArgumentException("KeyType")
        certificate = JsCertificate(cleanPem(pemEncodedCertificate))
        publicKey = certificate.publicKey
        keyId = certificate.kid
    }

    private fun cleanPem(input: String) = input
        .replace("-----BEGIN CERTIFICATE-----", "")
        .replace("-----END CERTIFICATE-----", "")
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .lines().joinToString(separator = "")

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, algorithmID),
        Pair(CoseHeaderKeys.KID, keyId)
    )

    override fun getCborSigningKey() = privateKey

    override fun getCborVerificationKey(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): ehn.techiop.hcert.kotlin.crypto.PubKey<*> {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        verificationResult.certificateValidFrom = certificate.validFrom
        verificationResult.certificateValidUntil = certificate.validUntil
        verificationResult.certificateValidContent = certificate.validContentTypes
        return publicKey
    }

    override fun getCertificate(): Certificate<*> = certificate

    override fun exportPrivateKeyAsPem() = "-----BEGIN PRIVATE KEY-----\n" +
            base64forPem(Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toByteArray()) +
            "\n-----END PRIVATE KEY-----\n"

    override fun exportCertificateAsPem() = "-----BEGIN CERTIFICATE-----\n" +
            base64forPem(Buffer((certificate.cert.toSchema() as Sequence).toBER()).toByteArray()) +
            "\n-----END CERTIFICATE-----\n"

    private fun base64forPem(encoded: ByteArray) =
        encoded.asBase64().chunked(64).joinToString(separator = "\n")

}


