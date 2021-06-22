package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseAdapter

class VerificationCoseService constructor(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        val coseAdapter = CoseAdapter(input)
        val kid = coseAdapter.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: coseAdapter.getUnprotectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "KID not found")
        //val algorithm = coseAdapter.getProtectedAttributeInt(CoseHeaderKeys.Algorithm.value)
        // TODO is the algorithm relevant?
        if (!coseAdapter.validate(kid, repository, verificationResult))
            throw VerificationException(Error.SIGNATURE_INVALID, "Not validated")
        return coseAdapter.getContent()
    }

}