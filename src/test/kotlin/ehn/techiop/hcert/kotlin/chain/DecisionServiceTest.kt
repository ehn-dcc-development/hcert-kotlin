package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.VerificationDecision.FAIL
import ehn.techiop.hcert.kotlin.chain.VerificationDecision.GOOD
import ehn.techiop.hcert.kotlin.trust.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class DecisionServiceTest {

    private val decisionService = DecisionService()

    @Test
    fun good() {
        val verificationResult = goodVerificationResult()

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun goodContentTest() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = true
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf(ContentType.TEST)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun goodContentVaccination() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = true
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun goodContentRecovery() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = true
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.RECOVERY)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun failContentTest() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = true
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf()
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun failContentVaccination() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = true
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.RECOVERY, ContentType.TEST)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun failContentRecovery() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = true
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun failBase45() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = false
            coseVerified = true
            cborDecoded = true
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun failCose() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = false
            cborDecoded = true
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun failCbor() {
        val verificationResult = VerificationResult().apply {
            base45Decoded = true
            coseVerified = true
            cborDecoded = false
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun issuedAtPast() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Instant.now().minusSeconds(5)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun issuedAtPastValidFromBeforeThat() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Instant.now().minusSeconds(5)
            certificateValidFrom = Instant.now().minusSeconds(10)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun issuedAtPastValidFromAfterThat() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Instant.now().minusSeconds(5)
            certificateValidFrom = Instant.now().minusSeconds(1)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun issuedAtPastExpirationFuture() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Instant.now().minusSeconds(5)
            expirationTime = Instant.now().plusSeconds(5)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun issuedAtFuture() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Instant.now().plusSeconds(5)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun expirationPast() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Instant.now().minusSeconds(5)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    @Test
    fun expirationFuture() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Instant.now().plusSeconds(5)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun expirationFutureValidUntilAfterThat() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Instant.now().plusSeconds(5)
            certificateValidUntil = Instant.now().plusSeconds(10)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(GOOD))
    }

    @Test
    fun expirationFutureValidUntilBeforeThat() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Instant.now().plusSeconds(5)
            certificateValidUntil = Instant.now().plusSeconds(2)
        }

        assertThat(decisionService.decide(verificationResult), equalTo(FAIL))
    }

    private fun goodVerificationResult() = VerificationResult().apply {
        base45Decoded = true
        coseVerified = true
        cborDecoded = true
    }


}