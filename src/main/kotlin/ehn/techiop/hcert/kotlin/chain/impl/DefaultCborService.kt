package ehn.techiop.hcert.kotlin.chain.impl

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.ContentType

/**
 * Encodes/decodes input as a CBOR structure
 */
open class DefaultCborService : CborService {

    override fun encode(input: Eudgc): ByteArray {
        return CBORMapper().writeValueAsBytes(input)
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): Eudgc {
        verificationResult.cborDecoded = false
        try {
            return CBORMapper()
                .readValue(input, Eudgc::class.java)
                .also { result ->
                    verificationResult.cborDecoded = true
                    if (result.t?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.TEST)
                    if (result.v?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.VACCINATION)
                    if (result.r?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.RECOVERY)
                }
        } catch (e: Throwable) {
            return Eudgc()
        }
    }

}