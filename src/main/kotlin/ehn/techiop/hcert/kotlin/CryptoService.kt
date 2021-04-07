package ehn.techiop.hcert.kotlin

import COSE.HeaderKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import java.security.cert.Certificate

interface CryptoService {

    fun getCborHeaders(): List<Pair<HeaderKeys, CBORObject>>

    fun getCborSigningKey(): OneKey

    fun getCborVerificationKey(kid: String): OneKey

    fun getCertificate(kid: String): Certificate


}
