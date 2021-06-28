package ehn.techiop.hcert.kotlin.crypto

import Asn1js.Sequence
import Asn1js.fromBER
import Buffer
import NodeRSA
import cose.CosePrivateKey
import cose.CosePublicKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.trust.ContentType
import elliptic.EC
import kotlinx.datetime.Clock
import org.khronos.webgl.Uint8Array
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.ECPrivateKey.ECPrivateKey
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo
import pkijs.src.RSAPrivateKey.RSAPrivateKey
import tsstdlib.JsonWebKey
import kotlin.js.Json


actual class CryptoAdapter {

    private val privateKeyInfo: PrivateKeyInfo
    actual val privateKey: PrivKey
    actual val publicKey: PubKey
    actual val algorithm: CwtAlgorithm
    actual val certificate: CertificateAdapter
    actual val privateKeyEncoded: ByteArray

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    actual constructor(keyType: KeyType, keySize: Int, contentType: List<ContentType>, clock: Clock) {
        when (keyType) {
            KeyType.EC -> {
                val ellipticName = if (keySize == 384) "p384" else "p256"
                algorithm = if (keySize == 384) CwtAlgorithm.ECDSA_384 else CwtAlgorithm.ECDSA_256
                val keyPair = EC(ellipticName).genKeyPair()
                privateKey = JsEcPrivKey(keyPair, keySize)
                publicKey = JsEcPubKey(keyPair, keySize)
                privateKeyInfo = PrivateKeyInfo()
                privateKeyInfo.fromJSON(privateKey.toPlatformPrivateKey())
                privateKeyEncoded = Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toByteArray()
                certificate = PkiUtils.selfSignCertificate("EC-Me", privateKey, publicKey, keySize, contentType, clock)
            }
            KeyType.RSA -> {
                val keyPair = NodeRSA().generateKeyPair(keySize)
                privateKey = JsRsaPrivKey(keyPair.exportKey("components-private") as Json)
                publicKey = JsRsaPubKey(keyPair.exportKey("components-public") as Json)
                privateKeyInfo = PrivateKeyInfo()
                privateKeyInfo.fromJSON(privateKey.toPlatformPrivateKey())
                privateKeyEncoded = Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toByteArray()
                algorithm = CwtAlgorithm.RSA_PSS_256
                certificate = PkiUtils.selfSignCertificate("RSA-Me", privateKey, publicKey, keySize, contentType, clock)
            }
        }
    }

    actual constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) {
        val array = cleanPem(pemEncodedPrivateKey).fromBase64().toTypedArray()
        privateKeyEncoded = array.toByteArray()
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
            if ((ecPrivateKey.namedCurve == "1.3.132.0.34" || content.length == 48)) {
                privateKey = JsEcPrivKey(EC("p384").keyFromPrivate(content), 384)
                algorithm = CwtAlgorithm.ECDSA_384
            } else {
                privateKey = JsEcPrivKey(EC("p256").keyFromPrivate(content), 256)
                algorithm = CwtAlgorithm.ECDSA_256
            }
        } else if (oid == "1.2.840.113549.1.1.1") {
            val buffer = Buffer(privateKeyInfo.privateKey.valueBlock.valueHex)
            val rsaPrivateKey = fromBER(buffer.buffer).result.let {
                RSAPrivateKey(js("({'schema':it})"))
            }
            privateKey = JsRsaPrivKey(rsaPrivateKey)
            algorithm = CwtAlgorithm.RSA_PSS_256
        } else throw IllegalArgumentException("KeyType")
        certificate = CertificateAdapter(pemEncodedCertificate)
        publicKey = certificate.publicKey
    }

    private fun cleanPem(input: String) = input
        .replace("-----BEGIN CERTIFICATE-----", "")
        .replace("-----END CERTIFICATE-----", "")
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .lines().joinToString(separator = "")

}

interface JsPubKey : PubKey {
    fun toPlatformPublicKey(): JsonWebKey
    fun toCoseRepresentation(): CosePublicKey
}

interface JsPrivKey : PrivKey {
    fun toCoseRepresentation(): CosePrivateKey
}
