package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseAdapter
import ehn.techiop.hcert.kotlin.trust.CoseCreationAdapter


/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
open class DefaultCoseService private constructor(
    private val signingService: CryptoService?,
    private val verificationRepo: CertificateRepository?
) : CoseService {

    constructor(signingService: CryptoService) : this(signingService, null)

    constructor(verificationRepo: CertificateRepository) : this(null, verificationRepo)

    override fun encode(input: ByteArray): ByteArray {
        if (signingService == null) throw NotImplementedError()
        val coseAdapter = CoseCreationAdapter(input)
        signingService.getCborHeaders().forEach {
            coseAdapter.addProtectedAttribute(it.first, it.second)
        }
        coseAdapter.sign(signingService.getCborSigningKey())
        return coseAdapter.encode()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        val coseAdapter = CoseAdapter(input)
        val kid = coseAdapter.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: coseAdapter.getUnprotectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "KID not found")
        // TODO is the algorithm relevant?
        //val algorithm = coseAdapter.getProtectedAttributeInt(CoseHeaderKeys.Algorithm.value)
        if (verificationRepo != null) {
            if (!coseAdapter.validate(kid, verificationRepo, verificationResult))
                throw VerificationException(Error.SIGNATURE_INVALID, "Not validated")
        } else if (signingService != null) {
            if (!coseAdapter.validate(kid, signingService, verificationResult))
                throw VerificationException(Error.SIGNATURE_INVALID, "Not validated")
        } else {
            // safe to throw this "ugly" error, as it should not happen
            throw NotImplementedError()
        }
        return coseAdapter.getContent()
    }

}
