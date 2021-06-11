package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey


/**
 * Uses a cryptographic key-pair to sign and verify COSE structures
 */
interface CryptoService {

    fun getCborHeaders(): List<Pair<CoseHeaderKeys, Any>>

    fun getCborSigningKey(): PrivKey

    fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult = VerificationResult()): PubKey

    fun getCertificate(): CertificateAdapter

    fun exportPrivateKeyAsPem(): String

    fun exportCertificateAsPem(): String

}
