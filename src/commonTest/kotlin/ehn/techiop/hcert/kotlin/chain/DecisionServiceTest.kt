package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.VerificationDecision.FAIL
import ehn.techiop.hcert.kotlin.chain.VerificationDecision.GOOD
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlin.time.Duration

private val decisionService = DecisionService()

private fun goodVerificationResult() = VerificationResult().apply {
    cborDecoded = true
}

class DecisionServiceTest : StringSpec({

    "good" {
        val verificationResult = goodVerificationResult()
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "goodContentTest" {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf(ContentType.TEST)
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "goodContentVaccination" {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "goodContentRecovery" {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.RECOVERY)
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "failContentTest" {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf()
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "failContentVaccination" {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.RECOVERY, ContentType.TEST)
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "failContentRecovery" {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "failCbor" {
        val verificationResult = goodVerificationResult().apply {
            cborDecoded = false
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "issuedAtPast" {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "issuedAtPastValidFromBeforeThat" {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(10))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "issuedAtPastValidFromAfterThat" {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(1))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "issuedAtPastExpirationFuture" {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "issuedAtFuture" {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().plus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "expirationPast" {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().minus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }

    "expirationFuture" {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "expirationFutureValidUntilAfterThat" {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(10))
        }
        decisionService.decide(verificationResult) shouldBe GOOD
    }

    "expirationFutureValidUntilBeforeThat" {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(1))
        }
        decisionService.decide(verificationResult) shouldBe FAIL
    }
})

