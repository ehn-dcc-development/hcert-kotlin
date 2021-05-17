package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CwtService
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
expect class DefaultCwtService @OptIn(ExperimentalTime::class) constructor(
    countryCode: String = "AT",
    validity: Duration = 48.toDuration(DurationUnit.HOURS),
    clock: Clock = Clock.System,
) : CwtService {

}