package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

expect class CryptoAdapter {

    constructor(keyType: KeyType = KeyType.EC, keySize: Int = 256, contentType: List<ContentType>, clock: Clock)

    constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String)

    val privateKey: PrivKey
    val publicKey: PubKey
    val algorithm: CwtAlgorithm
    val certificate: CertificateAdapter
    val privateKeyEncoded: ByteArray

}