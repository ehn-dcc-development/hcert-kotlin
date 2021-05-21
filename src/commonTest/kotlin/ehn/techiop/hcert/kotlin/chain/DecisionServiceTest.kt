package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.VerificationDecision.FAIL
import ehn.techiop.hcert.kotlin.chain.VerificationDecision.GOOD
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration


class DecisionServiceTest {

    private val decisionService = DecisionService()

    @Test
    fun good() {
        val verificationResult = goodVerificationResult()

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun goodContentTest() {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf(ContentType.TEST)
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun goodContentVaccination() {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun goodContentRecovery() {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.RECOVERY)
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun failContentTest() {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf()
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failContentVaccination() {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.RECOVERY, ContentType.TEST)
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failContentRecovery() {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failBase45() {
        val verificationResult = goodVerificationResult().apply {
            base45Decoded = false
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failCose() {
        val verificationResult = goodVerificationResult().apply {
            coseVerified = false
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failCbor() {
        val verificationResult = goodVerificationResult().apply {
            cborDecoded = false
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failCwt() {
        val verificationResult = goodVerificationResult().apply {
            cwtDecoded = false
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun failContextIdentifier() {
        val verificationResult = goodVerificationResult().apply {
            contextIdentifier = null
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun issuedAtPast() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun issuedAtPastValidFromBeforeThat() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(10))
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun issuedAtPastValidFromAfterThat() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(1))
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun issuedAtPastExpirationFuture() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun issuedAtFuture() {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().plus(Duration.seconds(5))
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun expirationPast() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().minus(Duration.seconds(5))
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    @Test
    fun expirationFuture() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun expirationFutureValidUntilAfterThat() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(10))
        }

        assertEquals(GOOD, decisionService.decide(verificationResult))
    }

    @Test
    fun expirationFutureValidUntilBeforeThat() {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(1))
        }

        assertEquals(FAIL, decisionService.decide(verificationResult))
    }

    private fun goodVerificationResult() = VerificationResult().apply {
        base45Decoded = true
        coseVerified = true
        cwtDecoded = true
        cborDecoded = true
        contextIdentifier = "HC1:"
    }


}