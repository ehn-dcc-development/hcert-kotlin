package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseAdapter

class VerificationCoseService constructor(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        try {
            val coseAdapter = CoseAdapter(input)
            val kid = coseAdapter.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
                ?: coseAdapter.getUnprotectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
                ?: throw IllegalArgumentException("KID not found").also {
                    verificationResult.error = Error.KEY_NOT_IN_TRUST_LIST
                }
            //val algorithm = coseAdapter.getProtectedAttributeInt(CoseHeaderKeys.Algorithm.value)
            // TODO is the algorithm relevant?
            if (!coseAdapter.validate(kid, repository, verificationResult)) {
                throw IllegalArgumentException("Not validated").also {
                    verificationResult.error = Error.SIGNATURE_INVALID
                }
            }
            return coseAdapter.getContent()
        } catch (e: Throwable) {
            throw e.also {
                verificationResult.error = Error.SIGNATURE_INVALID
            }
        }
    }

}