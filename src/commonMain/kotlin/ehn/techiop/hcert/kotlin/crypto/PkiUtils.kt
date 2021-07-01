package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock

expect object PkiUtils {

    fun selfSignCertificate(
        privateKey: PrivKey,
        publicKey: PubKey,
        keySize: Int,
        contentType: List<ContentType> = ContentType.values().toList(),
        clock: Clock = Clock.System
    ): CertificateAdapter

}