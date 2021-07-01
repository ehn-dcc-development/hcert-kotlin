package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.PrivKey

/**
 * Adapter to create and serialize COSE structures on all targets
 */
expect class CoseCreationAdapter constructor(content: ByteArray) {

    fun addProtectedAttribute(key: CoseHeaderKeys, value: Any)

    fun addUnprotectedAttribute(key: CoseHeaderKeys, value: Any)

    fun sign(key: PrivKey)

    fun encode(): ByteArray

}