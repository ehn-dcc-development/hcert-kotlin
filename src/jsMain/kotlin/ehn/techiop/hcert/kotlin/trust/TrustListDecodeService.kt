package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import kotlinx.datetime.Clock

actual class TrustListDecodeService actual constructor(repository: CertificateRepository, clock: Clock) {
    actual fun decode(input: ByteArray): TrustList {
        TODO("Not yet implemented")
    }
}