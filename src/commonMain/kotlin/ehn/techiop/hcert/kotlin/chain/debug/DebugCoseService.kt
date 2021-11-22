package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseAdapter
import ehn.techiop.hcert.kotlin.trust.CoseCreationAdapter


/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
open class DebugCoseService private constructor(
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
            //TODO: this is hacky!
            ?: throw NonFatalVerificationException(
                coseAdapter.getContent(),
                Error.KEY_NOT_IN_TRUST_LIST,
                "KID not found"
            )
        // TODO is the algorithm relevant?
        //val algorithm = coseAdapter.getProtectedAttributeInt(CoseHeaderKeys.Algorithm.value)
        if (verificationRepo != null) {
            if (!coseAdapter.validate(kid, verificationRepo, verificationResult))
                throw NonFatalVerificationException(coseAdapter.getContent(), Error.SIGNATURE_INVALID, "Not validated")
        } else if (signingService != null) {
            if (!coseAdapter.validate(kid, signingService, verificationResult))
                throw NonFatalVerificationException(coseAdapter.getContent(), Error.SIGNATURE_INVALID, "Not validated")
        } else {
            // safe to throw this "ugly" error, as it should not happen
            throw NotImplementedError()
        }
        return coseAdapter.getContent()
    }

    override fun getVerificationRepo() = verificationRepo
}
