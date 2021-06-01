package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseAdapter
import ehn.techiop.hcert.kotlin.trust.CoseCreationAdapter


/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
open class DefaultCoseService(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        val coseAdapter = CoseCreationAdapter(input)
        cryptoService.getCborHeaders().forEach {
            coseAdapter.addProtectedAttributeByteArray(it.first.value, it.second)
        }
        coseAdapter.sign(cryptoService.getCborSigningKey())
        return coseAdapter.encode()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        val coseAdapter = CoseAdapter(strippedInput(input))
        val kid = coseAdapter.getProtectedAttributeByteArray(CoseHeaderKeys.KID.value)
            ?: coseAdapter.getUnprotectedAttributeByteArray(CoseHeaderKeys.KID.value)
            ?: throw IllegalArgumentException("KID not found")
        val algorithm = coseAdapter.getProtectedAttributeInt(CoseHeaderKeys.Algorithm.value)
        // TODO is the algorithm relevant?
        val validate = coseAdapter.validate(kid, cryptoService, verificationResult)
        if (!validate) {
            throw IllegalArgumentException("Not validated")
        }
        return coseAdapter.getContent()
    }

    // Input may be tagged as a CWT and a Sign1
    private fun strippedInput(input: ByteArray): ByteArray {
        if (input.size >= 3 && input[0] == 0xD8.toByte() && input[1] == 0x3D.toByte() && input[2] == 0xD2.toByte())
            return input.drop(2).toByteArray()
        return input
    }

}
