package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.VerificationDecision.FAIL
import ehn.techiop.hcert.kotlin.chain.VerificationDecision.GOOD
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlin.time.Duration

private val decisionService = DecisionService()

class DecisionServiceTest : FunSpec({


    test("good") {
        val verificationResult = goodVerificationResult()

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("goodContentTest") {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf(ContentType.TEST)
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("goodContentVaccination") {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("goodContentRecovery") {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.RECOVERY)
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("failContentTest") {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.TEST)
            certificateValidContent = mutableListOf()
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failContentVaccination") {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.VACCINATION)
            certificateValidContent = mutableListOf(ContentType.RECOVERY, ContentType.TEST)
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failContentRecovery") {
        val verificationResult = goodVerificationResult().apply {
            content = mutableListOf(ContentType.RECOVERY)
            certificateValidContent = mutableListOf(ContentType.VACCINATION)
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failBase45") {
        val verificationResult = goodVerificationResult().apply {
            base45Decoded = false
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failCose") {
        val verificationResult = goodVerificationResult().apply {
            coseVerified = false
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failCbor") {
        val verificationResult = goodVerificationResult().apply {
            cborDecoded = false
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failCwt") {
        val verificationResult = goodVerificationResult().apply {
            cwtDecoded = false
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("failContextIdentifier") {
        val verificationResult = goodVerificationResult().apply {
            contextIdentifier = null
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("issuedAtPast") {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("issuedAtPastValidFromBeforeThat") {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(10))
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("issuedAtPastValidFromAfterThat") {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            certificateValidFrom = Clock.System.now().minus(Duration.seconds(1))
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("issuedAtPastExpirationFuture") {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().minus(Duration.seconds(5))
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("issuedAtFuture") {
        val verificationResult = goodVerificationResult().apply {
            issuedAt = Clock.System.now().plus(Duration.seconds(5))
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("expirationPast") {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().minus(Duration.seconds(5))
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }

    test("expirationFuture") {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("expirationFutureValidUntilAfterThat") {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(10))
        }

        (GOOD shouldBe decisionService.decide(verificationResult))
    }

    test("expirationFutureValidUntilBeforeThat") {
        val verificationResult = goodVerificationResult().apply {
            expirationTime = Clock.System.now().plus(Duration.seconds(5))
            certificateValidUntil = Clock.System.now().plus(Duration.seconds(1))
        }

        (FAIL shouldBe decisionService.decide(verificationResult))
    }
})

private fun goodVerificationResult() = VerificationResult().apply {
    base45Decoded = true
    coseVerified = true
    cwtDecoded = true
    cborDecoded = true
    contextIdentifier = "HC1:"
}


