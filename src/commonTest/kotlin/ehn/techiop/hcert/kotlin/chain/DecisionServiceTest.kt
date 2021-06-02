package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.VerificationDecision.FAIL
import ehn.techiop.hcert.kotlin.chain.VerificationDecision.GOOD
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlin.time.Duration

private val decisionService = DecisionService()

class DecisionServiceTest : StringSpec({

    "good" {
        val verificationResult = VerificationResult()
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "goodContentTest" {
        val verificationResult = VerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf(ContentType.TEST)
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "goodContentVaccination" {
        val verificationResult = VerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "goodContentRecovery" {
        val verificationResult = VerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.RECOVERY)
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "failContentTest" {
        val verificationResult = VerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf()
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "failContentVaccination" {
        val verificationResult = VerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.RECOVERY, ContentType.TEST)
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "failContentRecovery" {
        val verificationResult = VerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "issuedAtPast" {
        val verificationResult = VerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "issuedAtPastValidFromBeforeThat" {
        val verificationResult = VerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(10))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "issuedAtPastValidFromAfterThat" {
        val verificationResult = VerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(1))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "issuedAtPastExpirationFuture" {
        val verificationResult = VerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "issuedAtFuture" {
        val verificationResult = VerificationResult().apply {
            issuedAt = Clock.System.now().plus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "expirationPast" {
        val verificationResult = VerificationResult().apply {
            expirationTime = Clock.System.now().minus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "expirationFuture" {
        val verificationResult = VerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "expirationFutureValidUntilAfterThat" {
        val verificationResult = VerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(10))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "expirationFutureValidUntilBeforeThat" {
        val verificationResult = VerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(1))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }
})

