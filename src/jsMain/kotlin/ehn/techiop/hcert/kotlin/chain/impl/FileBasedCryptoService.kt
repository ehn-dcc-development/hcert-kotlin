package ehn.techiop.hcert.kotlin.chain.impl

import Asn1js.Sequence
import Asn1js.fromBER
import Buffer
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.JsEcPrivKey
import ehn.techiop.hcert.kotlin.crypto.JsRsaPrivKey
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import elliptic.EC
import org.khronos.webgl.Uint8Array
import pkijs.src.AlgorithmIdentifier.AlgorithmIdentifier
import pkijs.src.ECPrivateKey.ECPrivateKey
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo
import pkijs.src.RSAPrivateKey.RSAPrivateKey

actual class LoadedCryptoAdapter actual constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) {

    private val privateKeyInfo: PrivateKeyInfo
    actual val privateKey: PrivKey
    actual val algorithm: CwtAlgorithm
    actual val certificate: CertificateAdapter

    init {
        val array = cleanPem(pemEncodedPrivateKey).fromBase64().toTypedArray()
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
    }

    private fun cleanPem(input: String) = input
        .replace("-----BEGIN CERTIFICATE-----", "")
        .replace("-----END CERTIFICATE-----", "")
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .lines().joinToString(separator = "")


    actual val privateKeyEncoded: ByteArray = Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toByteArray()

}


