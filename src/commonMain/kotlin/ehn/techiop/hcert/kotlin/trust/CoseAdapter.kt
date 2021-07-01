package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

/**
 * Adapter to deserialize COSE structures on all targets
 */
expect class CoseAdapter constructor(input: ByteArray) {

    fun getProtectedAttributeByteArray(key: Int): ByteArray?

    fun getUnprotectedAttributeByteArray(key: Int): ByteArray?

    fun getProtectedAttributeInt(key: Int): Int?

    fun validate(kid: ByteArray, repository: CertificateRepository): Boolean

    fun validate(kid: ByteArray, repository: CertificateRepository, verificationResult: VerificationResult): Boolean

    fun validate(kid: ByteArray, cryptoService: CryptoService, verificationResult: VerificationResult): Boolean

    fun getContent(): ByteArray

    fun getContentMap(): CwtAdapter

}