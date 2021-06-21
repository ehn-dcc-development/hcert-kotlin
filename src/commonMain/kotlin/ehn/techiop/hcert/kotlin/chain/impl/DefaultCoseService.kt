package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.*
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
            coseAdapter.addProtectedAttribute(it.first, it.second)
        }
        coseAdapter.sign(cryptoService.getCborSigningKey())
        return coseAdapter.encode()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        val coseAdapter = try {
            CoseAdapter(strippedInput(input))
        } catch (e: Throwable) {
            throw VerificationException(Error.SIGNATURE_INVALID, e.message, e)
        }

        val kid = coseAdapter.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: coseAdapter.getUnprotectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "KID not found")

        //val algorithm = coseAdapter.getProtectedAttributeInt(CoseHeaderKeys.Algorithm.value)
        // TODO is the algorithm relevant?
        if (!coseAdapter.validate(kid, cryptoService, verificationResult))
            throw throw VerificationException(Error.SIGNATURE_INVALID, "Not validated")

        return coseAdapter.getContent()
    }

    // Input may be tagged as a CWT and a Sign1
    private fun strippedInput(input: ByteArray): ByteArray {
        if (input.size >= 3 && input[0] == 0xD8.toByte() && input[1] == 0x3D.toByte() && input[2] == 0xD2.toByte())
            return input.drop(2).toByteArray()
        return input
    }

}
