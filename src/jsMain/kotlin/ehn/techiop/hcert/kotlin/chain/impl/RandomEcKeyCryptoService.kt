package ehn.techiop.hcert.kotlin.chain.impl

import Asn1js.Sequence
import Buffer
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.common.selfSignCertificate
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.trust.ContentType
import elliptic.EC
import kotlinx.datetime.Clock
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo


actual class RandomEcKeyCryptoService actual constructor(
    val keySize: Int,
    contentType: List<ContentType>,
    clock: Clock
) : CryptoService {

    private val privateKeyInfo: PrivateKeyInfo
    private val privateKey: EcPrivKey<*>
    private val publicKey: EcPubKey<*>
    private val algorithmID: CwtAlgorithm
    private val certificate: JsCertificate
    private val keyId: ByteArray

    init {
        algorithmID = when (keySize) {
            256 -> CwtAlgorithm.ECDSA_256
            384 -> CwtAlgorithm.ECDSA_384
            else -> throw IllegalArgumentException("KeySize: $keySize")
        }
        val ellipticName = if (algorithmID == CwtAlgorithm.ECDSA_384) "p384" else "p256"
        val keyPair = EC(ellipticName).genKeyPair()
        privateKey = JsEcPrivKey(keyPair)
        publicKey = JsEcPubKey(keyPair)
        certificate = selfSignCertificate("EC-Me", privateKey, publicKey, keySize, contentType, clock) as JsCertificate
        keyId = certificate.kid
        privateKeyInfo = PrivateKeyInfo()
        @Suppress("UNUSED_VARIABLE") val d = keyPair.getPrivate().toArrayLike(Buffer).toString("base64")
        if (algorithmID == CwtAlgorithm.ECDSA_384)
            privateKeyInfo.fromJSON(js("({'kty': 'EC', 'crv':'P-384', 'd': d})"))
        else
            privateKeyInfo.fromJSON(js("({'kty': 'EC', 'crv':'P-256', 'd': d})"))
    }

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, algorithmID),
        Pair(CoseHeaderKeys.KID, keyId)
    )

    override fun getCborSigningKey() = privateKey

    override fun getCborVerificationKey(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): PubKey<*> {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid").also {
            verificationResult.error = Error.KEY_NOT_IN_TRUST_LIST
        }
        verificationResult.setCertificateData(certificate)
        return publicKey
    }

    override fun getCertificate(): CertificateAdapter<*> = certificate

    override fun exportPrivateKeyAsPem() = "-----BEGIN PRIVATE KEY-----\n" +
            base64forPem(Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toByteArray()) +
            "\n-----END PRIVATE KEY-----\n"

    override fun exportCertificateAsPem() = "-----BEGIN CERTIFICATE-----\n" +
            base64forPem(certificate.encoded) +
            "\n-----END CERTIFICATE-----\n"

    private fun base64forPem(encoded: ByteArray) =
        encoded.asBase64().chunked(64).joinToString(separator = "\n")

}


