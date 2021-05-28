package ehn.techiop.hcert.kotlin.chain.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import ehn.techiop.hcert.data.Eudcc
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.ContentType

/**
 * Encodes/decodes input as a CBOR structure
 */
open class DefaultCborService : CborService {

    override fun encode(input: Eudcc): ByteArray {
        return CBORMapper().writeValueAsBytes(input)
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): Eudcc {
        verificationResult.cborDecoded = false
        try {
            return CBORMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(input, Eudcc::class.java)
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
            return Eudcc()
        }
    }

}