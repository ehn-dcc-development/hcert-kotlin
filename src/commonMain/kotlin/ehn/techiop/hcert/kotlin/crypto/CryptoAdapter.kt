package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

expect class CryptoAdapter(
    keyType: KeyType = KeyType.EC,
    keySize: Int = 256,
    contentType: List<ContentType>,
    clock: Clock,
) {

    val privateKey: PrivKey
    val algorithm: CwtAlgorithm
    val certificate: CertificateAdapter
    val privateKeyEncoded: ByteArray

}