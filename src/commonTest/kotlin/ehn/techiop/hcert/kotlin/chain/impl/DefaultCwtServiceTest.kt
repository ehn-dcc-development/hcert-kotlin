package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SampleData
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.ext.FixedClock
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CwtCreationAdapter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DefaultCwtServiceTest : FunSpec({

    test("cwt may be longer valid than the certificate") {
        val timeOfCheck = Instant.parse("2021-01-02T12:00:00Z")
        val cwtIssuedAt = Instant.parse("2021-01-01T12:00:00Z")
        val cwtExpiratn = Instant.parse("2021-01-30T12:00:00Z")
        val certNoBefor = Instant.parse("2021-01-01T12:00:00Z")
        val certNoAfter = Instant.parse("2021-01-20T12:00:00Z")

        val decoded = DefaultCwtService(clock = FixedClock(timeOfCheck)).decode(
            encodeCwt(cwtIssuedAt, cwtExpiratn),
            buildVerificationResult(certNoBefor, certNoAfter)
        )

        decoded shouldNotBe null
    }

    test("certificate may be longer valid than the cwt") {
        val timeOfCheck = Instant.parse("2021-01-02T12:00:00Z")
        val cwtIssuedAt = Instant.parse("2021-01-01T12:00:00Z")
        val cwtExpiratn = Instant.parse("2021-01-03T12:00:00Z")
        val certNoBefor = Instant.parse("2021-01-01T12:00:00Z")
        val certNoAfter = Instant.parse("2021-01-20T12:00:00Z")

        val decoded = DefaultCwtService(clock = FixedClock(timeOfCheck)).decode(
            encodeCwt(cwtIssuedAt, cwtExpiratn),
            buildVerificationResult(certNoBefor, certNoAfter)
        )

        decoded shouldNotBe null
    }

    test("cert valid from must be before now") {
        val timeOfCheck = Instant.parse("2021-01-02T12:00:00Z")
        val cwtIssuedAt = Instant.parse("2021-01-01T12:00:00Z")
        val cwtExpiratn = Instant.parse("2021-01-03T12:00:00Z")
        val certNoBefor = Instant.parse("2021-01-10T12:00:00Z")
        val certNoAfter = Instant.parse("2021-01-20T12:00:00Z")

        val exception = shouldThrow<VerificationException> {
            DefaultCwtService(clock = FixedClock(timeOfCheck)).decode(
                encodeCwt(cwtIssuedAt, cwtExpiratn),
                buildVerificationResult(certNoBefor, certNoAfter)
            )
        }

        exception.error shouldBe Error.PUBLIC_KEY_NOT_YET_VALID
    }

    test("cert valid until must be after now") {
        val timeOfCheck = Instant.parse("2021-01-02T12:00:00Z")
        val cwtIssuedAt = Instant.parse("2021-01-01T12:00:00Z")
        val cwtExpiratn = Instant.parse("2021-01-03T12:00:00Z")
        val certNoBefor = Instant.parse("2021-01-01T12:00:00Z")
        val certNoAfter = Instant.parse("2021-01-01T12:00:00Z")

        val exception = shouldThrow<VerificationException> {
            DefaultCwtService(clock = FixedClock(timeOfCheck)).decode(
                encodeCwt(cwtIssuedAt, cwtExpiratn),
                buildVerificationResult(certNoBefor, certNoAfter)
            )
        }

        exception.error shouldBe Error.PUBLIC_KEY_EXPIRED
    }

    test("cwt issued at must be before now") {
        val timeOfCheck = Instant.parse("2021-01-02T12:00:00Z")
        val cwtIssuedAt = Instant.parse("2021-01-03T12:00:00Z")
        val cwtExpiratn = Instant.parse("2021-01-05T12:00:00Z")
        val certNoBefor = Instant.parse("2021-01-01T12:00:00Z")
        val certNoAfter = Instant.parse("2021-01-20T12:00:00Z")

        val exception = shouldThrow<VerificationException> {
            DefaultCwtService(clock = FixedClock(timeOfCheck)).decode(
                encodeCwt(cwtIssuedAt, cwtExpiratn),
                buildVerificationResult(certNoBefor, certNoAfter)
            )
        }

        exception.error shouldBe Error.CWT_NOT_YET_VALID
    }

    test("cwt expired must be after now") {
        val timeOfCheck = Instant.parse("2021-01-02T12:00:00Z")
        val cwtIssuedAt = Instant.parse("2021-01-01T12:00:00Z")
        val cwtExpiratn = Instant.parse("2021-01-01T12:00:00Z")
        val certNoBefor = Instant.parse("2021-01-01T12:00:00Z")
        val certNoAfter = Instant.parse("2021-01-20T12:00:00Z")

        val exception = shouldThrow<VerificationException> {
            DefaultCwtService(clock = FixedClock(timeOfCheck)).decode(
                encodeCwt(cwtIssuedAt, cwtExpiratn),
                buildVerificationResult(certNoBefor, certNoAfter)
            )
        }

        exception.error shouldBe Error.CWT_EXPIRED
    }

})

private fun encodeCwt(
    cwtValidFrom: Instant,
    cwtValidUntil: Instant
): ByteArray {
    val cwtAdapter = CwtCreationAdapter()
    cwtAdapter.add(CwtHeaderKeys.ISSUED_AT.intVal, cwtValidFrom.epochSeconds)
    cwtAdapter.add(CwtHeaderKeys.EXPIRATION.intVal, cwtValidUntil.epochSeconds)
    cwtAdapter.addDgc(
        CwtHeaderKeys.HCERT.intVal,
        CwtHeaderKeys.EUDGC_IN_HCERT.intVal,
        DefaultCborService().encode(Json.decodeFromString(SampleData.vaccination))
    )
    val cwtEncoded = cwtAdapter.encode()
    return cwtEncoded
}

private fun buildVerificationResult(
    certValidFrom: Instant,
    certValidUntil: Instant
): VerificationResult {
    val verificationResult = VerificationResult().apply {
        certificateValidFrom = certValidFrom
        certificateValidUntil = certValidUntil
    }
    return verificationResult
}

