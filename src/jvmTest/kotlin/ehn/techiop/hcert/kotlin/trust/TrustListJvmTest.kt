package ehn.techiop.hcert.kotlin.trust

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.ext.FixedClock
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.TrustListCertificateRepository
import ehn.techiop.hcert.kotlin.crypto.Certificate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


class TrustListJvmTest : StringSpec({

    "V2 Client-Server Exchange" {
        val clock = FixedClock(Instant.fromEpochMilliseconds(0))
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val certificate = cryptoService.getCertificate()
        val encodeService = TrustListV2EncodeService(cryptoService, clock = clock)
        val trustListEncoded = encodeService.encodeContent(randomCertificates(clock))
        val trustListSignature = encodeService.encodeSignature(trustListEncoded)

        verifyClientOperations(certificate, clock, trustListSignature, trustListEncoded)
    }


})

private fun verifyClientOperations(
    certificate: Certificate<*>,
    clock: Clock,
    trustListSignature: ByteArray,
    trustListEncoded: ByteArray? = null
) {
    // might never happen on the client, that the trust list is loaded in this way
    val clientTrustRoot = PrefilledCertificateRepository(certificate)
    val decodeService = TrustListDecodeService(clientTrustRoot, clock = clock)
    val clientTrustList = decodeService.decode(trustListSignature, trustListEncoded)
    // that's the way to go: Trust list used for verification of QR codes
    val clientTrustListAdapter =
        TrustListCertificateRepository(trustListSignature, trustListEncoded, clientTrustRoot, clock)

    clientTrustList.size shouldBe 2
    for (cert in clientTrustList) {
        cert.validFrom.epochSeconds shouldBeLessThanOrEqual clock.now().epochSeconds
        cert.validUntil.epochSeconds shouldBeGreaterThanOrEqual clock.now().epochSeconds
        cert.kid.size shouldBe 8
        cert.validContentTypes.size shouldBe 3

        clientTrustListAdapter.loadTrustedCertificates(cert.kid, VerificationResult()).forEach {
            val loadedEncoding = (it.cosePublicKey.toCoseRepresentation() as OneKey).EncodeToBytes()
            val certEncoding = (cert.cosePublicKey.toCoseRepresentation() as OneKey).EncodeToBytes()
            loadedEncoding shouldBe certEncoding
        }
    }
}


private fun randomCertificates(clock: Clock): Set<Certificate<*>> =
    listOf(RandomEcKeyCryptoService(clock = clock), RandomRsaKeyCryptoService(clock = clock))
        .map { it.getCertificate() }
        .toSet()
