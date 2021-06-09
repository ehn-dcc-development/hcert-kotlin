package ehn.techiop.hcert.kotlin.chain.impl

import Asn1js.Sequence
import Buffer
import NodeRSA
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.common.selfSignCertificate
import ehn.techiop.hcert.kotlin.chain.common.urlSafe
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int32Array
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo
import tsstdlib.JsonWebKey
import kotlin.js.Json

actual class RandomRsaKeyCryptoService actual constructor(
    keySize: Int,
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
        val keyPair = NodeRSA().generateKeyPair(keySize)
        val pub = keyPair.exportKey("components-public") as Json
        val modulus = pub["n"] as Buffer
        val exp = pub["e"] as Number

        publicKey = JsRsaPubKey(ArrayBuffer.from(modulus.toByteArray()), exp)
        privateKey = JsRsaPrivKey(keyPair.exportKey("components-private") as Json)
        algorithmID = CwtAlgorithm.RSA_PSS_256
        certificate = selfSignCertificate("RSA-Me", privateKey, publicKey, contentType, clock) as JsCertificate
        keyId = certificate.kid
        privateKeyInfo = PrivateKeyInfo()

        val cr = privateKey.toCoseRepresentation()
        val jwk = object : JsonWebKey {
            override var alg: String? = "PS256"
            override var kty: String? = "RSA"
            override var p: String? = urlSafe(cr.p.toString("base64"))
            override var q: String? = urlSafe(cr.q.toString("base64"))
            override var qi: String? = urlSafe(cr.qi.toString("base64"))
            override var dp: String? = urlSafe(cr.dp.toString("base64"))
            override var dq: String? = urlSafe(cr.dq.toString("base64"))
            override var e: String? = urlSafe(Buffer(Int32Array(arrayOf(cr.e.toInt())).buffer).toString("base64"))
            override var n: String? = urlSafe(cr.n.toString("base64"))
            override var d: String? = urlSafe(cr.d.toString("base64"))
        }

        privateKeyInfo.fromJSON(jwk as JsonWebKey)
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