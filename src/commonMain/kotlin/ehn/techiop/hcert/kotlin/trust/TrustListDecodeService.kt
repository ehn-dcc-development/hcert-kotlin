package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import kotlinx.datetime.Clock

expect class TrustListDecodeService(repository: CertificateRepository, clock: Clock = Clock.System) {
    fun decode(input: ByteArray): TrustList
}