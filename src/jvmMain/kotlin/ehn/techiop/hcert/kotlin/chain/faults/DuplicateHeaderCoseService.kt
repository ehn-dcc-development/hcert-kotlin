package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseCreationAdapter

/**
 * Puts the KID header entry into the unprotected *and* protected COSE header.
 *
 * Actually, this conforms to the specification, but we'll prefer to put the entries into the protected COSE header.
 */
class DuplicateHeaderCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        val coseAdapter = CoseCreationAdapter(input)
        cryptoService.getCborHeaders().forEach {
            coseAdapter.addProtectedAttribute(it.first, it.second)
            if (it.first == CoseHeaderKeys.KID)
                coseAdapter.addUnprotectedAttribute(it.first, it.second)
        }
        coseAdapter.sign(cryptoService.getCborSigningKey())
        return coseAdapter.encode()
    }

}