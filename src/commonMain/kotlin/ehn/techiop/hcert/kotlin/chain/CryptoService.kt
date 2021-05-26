package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.PrivateKey
import ehn.techiop.hcert.kotlin.crypto.PublicKey


interface CryptoService {

    fun getCborHeaders(): List<Pair<CoseHeaderKeys, Any>>

    fun getCborSigningKey(): PrivateKey<*>

    fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult = VerificationResult()): PublicKey<*>

    fun getCertificate(): Certificate<*>

    fun exportPrivateKeyAsPem(): String

    fun exportCertificateAsPem(): String

}
