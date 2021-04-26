package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.TrustListCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCoseService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test
import java.time.Instant

class TrustListTest {

    @Test
    fun serverClientExchange() {
        val cryptoService = RandomEcKeyCryptoService()
        val certificate = cryptoService.getCertificate()
        val trustListEncoded = TrustListEncodeService(cryptoService).encode(randomCertificates())

        // might never happen on the client, that the trust list is loaded in this way
        val clientTrustRoot = PrefilledCertificateRepository(certificate)
        val clientTrustList = TrustListDecodeService(VerificationCoseService(clientTrustRoot)).decode(trustListEncoded)
        // that's the way to go: Trust List used for verification of QR-codes
        val clientTrustListAdapter = TrustListCertificateRepository(trustListEncoded, clientTrustRoot)

        assertThat(clientTrustList.validFrom.epochSecond, lessThanOrEqualTo(Instant.now().epochSecond))
        assertThat(clientTrustList.validUntil.epochSecond, greaterThanOrEqualTo(Instant.now().epochSecond))
        assertThat(clientTrustList.certificates.size, equalTo(2))
        for (cert in clientTrustList.certificates) {
            assertThat(cert.validFrom.epochSecond, lessThanOrEqualTo(Instant.now().epochSecond))
            assertThat(cert.validUntil.epochSecond, greaterThanOrEqualTo(Instant.now().epochSecond))
            assertThat(cert.kid.size, equalTo(8))
            assertThat(cert.publicKey.size, greaterThanOrEqualTo(32))
            assertThat(cert.certType.size, equalTo(3))

            clientTrustListAdapter.loadTrustedCertificates(cert.kid, VerificationResult()).forEach {
                assertThat(it.publicKey, equalTo(cert.publicKey))
            }
        }
    }

    private fun randomCertificates() =
        listOf(RandomEcKeyCryptoService(), RandomRsaKeyCryptoService())
            .map { it.getCertificate() }
            .toSet()

}
