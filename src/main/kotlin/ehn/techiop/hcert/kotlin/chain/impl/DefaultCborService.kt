package ehn.techiop.hcert.kotlin.chain.impl

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType

/**
 * Encodes/decodes input as a CBOR structure
 */
open class DefaultCborService : CborService {

    override fun encode(input: GreenCertificate): ByteArray {
        return CBORMapper().writeValueAsBytes(input)
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate {
        verificationResult.cborDecoded = false
       // try {
            return CBORMapper()
                .readValue(input, GreenCertificate::class.java)
                .also { result ->

                    verificationResult.cborDecoded = true
                    if (result.tests?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.TEST)
                    if (result.vaccinations?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.VACCINATION)
                    if (result.recoveryStatements?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.RECOVERY)
                }
//        } catch (e: Throwable) {
//            return GreenCertificate()
//        }
    }

}