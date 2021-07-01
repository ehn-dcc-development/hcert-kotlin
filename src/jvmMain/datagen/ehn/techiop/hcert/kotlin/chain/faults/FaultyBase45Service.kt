package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service

/**
 * Produces a wrong Base45 encoding.
 *
 * **Should not be used in production.**
 */
class FaultyBase45Service : DefaultBase45Service() {

    private val encoder = Base45Encoder

    override fun encode(input: ByteArray) =
        encoder.encode(input).dropLast(5) + "====="

}