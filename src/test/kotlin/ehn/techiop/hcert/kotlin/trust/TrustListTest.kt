package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.TrustListCertificateRepository
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test
import java.security.cert.X509Certificate
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class TrustListTest {

    @Test
    fun serverClientExchange() {
        val clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val certificate = cryptoService.getCertificate()
        val trustListEncoded = TrustListEncodeService(cryptoService, clock = clock).encode(randomCertificates(clock))

        // might never happen on the client, that the trust list is loaded in this way
        val clientTrustRoot = PrefilledCertificateRepository(certificate)
        val clientTrustList = TrustListDecodeService(clientTrustRoot, clock = clock).decode(trustListEncoded)
        // that's the way to go: Trust list used for verification of QR codes
        val clientTrustListAdapter = TrustListCertificateRepository(trustListEncoded, clientTrustRoot, clock)

        assertThat(clientTrustList.validFrom.epochSecond, lessThanOrEqualTo(clock.instant().epochSecond))
        assertThat(clientTrustList.validUntil.epochSecond, greaterThanOrEqualTo(clock.instant().epochSecond))
        assertThat(clientTrustList.certificates.size, equalTo(2))
        for (cert in clientTrustList.certificates) {
            assertThat(cert.validFrom.epochSecond, lessThanOrEqualTo(clock.instant().epochSecond))
            assertThat(cert.validUntil.epochSecond, greaterThanOrEqualTo(clock.instant().epochSecond))
            assertThat(cert.kid.size, equalTo(8))
            assertThat(cert.publicKey.size, greaterThanOrEqualTo(32))
            assertThat(cert.validContentTypes.size, equalTo(3))

            clientTrustListAdapter.loadTrustedCertificates(cert.kid, VerificationResult()).forEach {
                assertThat(it.publicKey, equalTo(cert.publicKey))
            }
        }
    }

    private fun randomCertificates(clock: Clock): Set<X509Certificate> =
        listOf(RandomEcKeyCryptoService(clock = clock), RandomRsaKeyCryptoService(clock = clock))
            .map { it.getCertificate() }
            .toSet()

}