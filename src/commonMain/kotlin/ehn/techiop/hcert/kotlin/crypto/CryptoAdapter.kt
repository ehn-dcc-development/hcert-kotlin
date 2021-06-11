package ehn.techiop.hcert.kotlin.crypto

expect class CryptoAdapter constructor(
    keyType: KeyType = KeyType.EC,
    keySize: Int = 256,
) {

    val privateKey: PrivKey
    val publicKey: PubKey
    val privateKeyBase64: String

}