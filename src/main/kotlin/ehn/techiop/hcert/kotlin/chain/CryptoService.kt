package ehn.techiop.hcert.kotlin.chain

import COSE.HeaderKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import java.security.cert.X509Certificate

interface CryptoService {

    fun getCborHeaders(): List<Pair<HeaderKeys, CBORObject>>

    fun getCborSigningKey(): OneKey

    fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult = VerificationResult()): OneKey

    fun getCertificate(): X509Certificate


}
