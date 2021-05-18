package ehn.techiop.hcert.kotlin.chain.ext

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FixedClock(private val now: Instant) : Clock {
    override fun now() = now
}