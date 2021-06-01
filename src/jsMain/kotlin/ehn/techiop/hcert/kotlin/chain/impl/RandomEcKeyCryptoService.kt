package ehn.techiop.hcert.kotlin.chain.impl

import Asn1js.Sequence
import Buffer
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.common.selfSignCertificate
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.JsCertificate
import ehn.techiop.hcert.kotlin.crypto.JsEcPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsEcPubKey
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo


actual class RandomEcKeyCryptoService actual constructor(
    val keySize: Int,
    contentType: List<ContentType>,
    clock: Clock
) : CryptoService {

    private val privateKeyInfo: PrivateKeyInfo
    private val privateKey: PrivKey<*>
    private val publicKey: PubKey<*>
    private val algorithmID: CwtAlgorithm
    private val certificate: JsCertificate
    private val keyId: ByteArray

    init {
        val keyPair = js("require('elliptic').ec('p256').genKeyPair()")
        privateKey = JsEcPrivKey(keyPair)
        publicKey =
            JsEcPubKey(js("keyPair.getPublic().getX().toArrayLike(Buffer)") as Buffer, js("keyPair.getPublic().getY().toArrayLike(Buffer)") as Buffer)
        algorithmID = CwtAlgorithm.ECDSA_256
        certificate = selfSignCertificate("EC-Me", privateKey, publicKey, contentType, clock) as JsCertificate
        keyId = certificate.kid
        privateKeyInfo = PrivateKeyInfo()
        @Suppress("UNUSED_VARIABLE") val d = keyPair.getPrivate().toArrayLike(Buffer).toByteArray().asBase64()
        privateKeyInfo.fromJSON(js("({'crv':'P-256', 'd': d})"))
    }

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.Algorithm, algorithmID.value),
        Pair(CoseHeaderKeys.KID, keyId)
    )

    override fun getCborSigningKey() = privateKey

    override fun getCborVerificationKey(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): PubKey<*> {
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
            base64forPem(certificate.encoded) +
            "\n-----END CERTIFICATE-----\n"

    private fun base64forPem(encoded: ByteArray) =
        encoded.asBase64().chunked(64).joinToString(separator = "\n")

}


