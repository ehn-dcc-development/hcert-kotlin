package ehn.techiop.hcert.kotlin.crypto

import Asn1js.Sequence
import Buffer
import NodeRSA
import ehn.techiop.hcert.kotlin.chain.from
import ehn.techiop.hcert.kotlin.chain.toByteArray
import elliptic.EC
import org.khronos.webgl.ArrayBuffer
import pkijs.src.PrivateKeyInfo.PrivateKeyInfo
import tsstdlib.JsonWebKey
import kotlin.js.Json

actual class CryptoAdapter actual constructor(keyType: KeyType, keySize: Int) {

    private val privateKeyInfo: PrivateKeyInfo
    actual val privateKey: PrivKey<*>
    actual val publicKey: PubKey<*>

    init {
        if (keyType == KeyType.EC) {
            val ellipticName = if (keySize == 384) "p384" else "p256"
            val keyPair = EC(ellipticName).genKeyPair()
            privateKey = JsEcPrivKey(keyPair, keySize)
            publicKey = JsEcPubKey(keyPair, keySize)
            privateKeyInfo = PrivateKeyInfo()
            privateKeyInfo.fromJSON(privateKey.toPlatformPrivateKey() as JsonWebKey)
        } else if (keyType == KeyType.RSA) {
            val keyPair = NodeRSA().generateKeyPair(keySize)
            val pub = keyPair.exportKey("components-public") as Json
            val modulus = pub["n"] as Buffer
            val exp = pub["e"] as Number

            publicKey = JsRsaPubKey(ArrayBuffer.from(modulus.toByteArray()), exp)
            privateKey = JsRsaPrivKey(keyPair.exportKey("components-private") as Json)
            privateKeyInfo = PrivateKeyInfo()

            privateKeyInfo.fromJSON(privateKey.toPlatformPrivateKey())
        } else {
            throw IllegalArgumentException("KeyType")
        }
    }

    actual val privateKeyBase64: String
        get() = Buffer((privateKeyInfo.toSchema() as Sequence).toBER()).toString("base64")

}