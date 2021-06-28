package ehn.techiop.hcert.kotlin.crypto

import Asn1js.Sequence
import Buffer
import NodeRSA
import cose.CosePrivateKey
import cose.CosePublicKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.trust.ContentType
import elliptic.EC
import kotlinx.datetime.Clock
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo
import tsstdlib.JsonWebKey
import kotlin.js.Json


actual class CryptoAdapter actual constructor(
    keyType: KeyType,
    keySize: Int,
    contentType: List<ContentType>,
    clock: Clock
) {

    private val privateKeyInfo: PrivateKeyInfo
    actual val privateKey: PrivKey
    actual val algorithm: CwtAlgorithm
    actual val certificate: CertificateAdapter

    init {
        when (keyType) {
            KeyType.EC -> {
                val ellipticName = if (keySize == 384) "p384" else "p256"
                algorithm = if (keySize == 384) CwtAlgorithm.ECDSA_384 else CwtAlgorithm.ECDSA_256
                val keyPair = EC(ellipticName).genKeyPair()
                privateKey = JsEcPrivKey(keyPair, keySize)
                val publicKey = JsEcPubKey(keyPair, keySize)
                privateKeyInfo = PrivateKeyInfo()
                privateKeyInfo.fromJSON(privateKey.toPlatformPrivateKey())
                certificate = PkiUtils.selfSignCertificate("EC-Me", privateKey, publicKey, keySize, contentType, clock)
            }
            KeyType.RSA -> {
                val keyPair = NodeRSA().generateKeyPair(keySize)
                @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
                val publicKey = JsRsaPubKey(keyPair.exportKey("components-public") as Json)
                @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
                privateKey = JsRsaPrivKey(keyPair.exportKey("components-private") as Json)
                privateKeyInfo = PrivateKeyInfo()
                privateKeyInfo.fromJSON(privateKey.toPlatformPrivateKey())
                algorithm = CwtAlgorithm.RSA_PSS_256
                certificate = PkiUtils.selfSignCertificate("RSA-Me", privateKey, publicKey, keySize, contentType, clock)
            }
        }
    }

    actual val privateKeyEncoded: ByteArray
        get() = Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toByteArray()

}

interface JsPubKey : PubKey {
    fun toPlatformPublicKey(): JsonWebKey
    fun toCoseRepresentation(): CosePublicKey
}

interface JsPrivKey : PrivKey {
    fun toCoseRepresentation(): CosePrivateKey
}
