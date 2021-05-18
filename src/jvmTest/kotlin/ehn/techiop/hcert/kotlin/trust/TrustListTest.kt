package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.TrustListCertificateRepository
import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test
import java.security.cert.X509Certificate

@OptIn(ExperimentalSerializationApi::class)
class TrustListTest {

    @Test
    fun serverClientExchange() {
        val clock = Clock.System // TODO Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val certificate = cryptoService.getCertificate()
        val trustListEncoded = TrustListEncodeService(cryptoService, clock = clock).encode(randomCertificates(clock))

        // might never happen on the client, that the trust list is loaded in this way
        val clientTrustRoot = PrefilledCertificateRepository(certificate)
        val clientTrustList = TrustListDecodeService(clientTrustRoot, clock = clock).decode(trustListEncoded)
        // that's the way to go: Trust list used for verification of QR codes
        val clientTrustListAdapter = TrustListCertificateRepository(trustListEncoded, clientTrustRoot, clock)

        assertThat(
            clientTrustList.validFrom.epochSeconds,
            lessThanOrEqualTo(clock.now().epochSeconds)
        )
        assertThat(
            clientTrustList.validUntil.epochSeconds,
            greaterThanOrEqualTo(clock.now().epochSeconds)
        )
        assertThat(clientTrustList.certificates.size, CoreMatchers.equalTo(2))
        for (cert in clientTrustList.certificates) {
            assertThat(
                cert.validFrom.epochSeconds,
                lessThanOrEqualTo(clock.now().epochSeconds)
            )
            assertThat(
                cert.validUntil.epochSeconds,
                greaterThanOrEqualTo(clock.now().epochSeconds)
            )
            assertThat(cert.kid.size, CoreMatchers.equalTo(8))
            assertThat(cert.publicKey.size, greaterThanOrEqualTo(32))
            assertThat(cert.validContentTypes.size, CoreMatchers.equalTo(3))

            clientTrustListAdapter.loadTrustedCertificates(cert.kid, VerificationResult()).forEach {
                assertThat(it.publicKey, CoreMatchers.equalTo(cert.publicKey))
            }
        }
    }

    private fun randomCertificates(clock: Clock): Set<X509Certificate> =
        listOf(RandomEcKeyCryptoService(clock = clock), RandomRsaKeyCryptoService(clock = clock))
            .map { (it.getCertificate() as JvmCertificate).certificate }
            .toSet()

}