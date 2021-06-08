package ehn.techiop.hcert.kotlin.chain.common

import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock


expect fun selfSignCertificate(
    commonName: String,
    privateKey: PrivKey<*>,
    publicKey: PubKey<*>,
    keySize: Int,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
): Certificate<*>



