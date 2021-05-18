package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CwtService
import kotlinx.datetime.Clock
import kotlin.time.Duration

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
expect class DefaultCwtService constructor(
    countryCode: String = "AT",
    validity: Duration = Duration.hours(48),
    clock: Clock = Clock.System,
) : CwtService {

}