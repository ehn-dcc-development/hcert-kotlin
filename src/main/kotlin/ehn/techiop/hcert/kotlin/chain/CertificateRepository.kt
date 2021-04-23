package ehn.techiop.hcert.kotlin.chain

import java.security.PublicKey

interface CertificateRepository {

    fun loadPublicKey(kid: ByteArray, verificationResult: VerificationResult): PublicKey

}