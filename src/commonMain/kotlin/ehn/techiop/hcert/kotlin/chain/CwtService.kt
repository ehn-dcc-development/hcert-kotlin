package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.CborObject

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
interface CwtService {

    fun encode(input: ByteArray): ByteArray

    /**
     * Throws a Throwable if schema validation fails
     */
    fun decode(input: ByteArray, verificationResult: VerificationResult): CborObject

}