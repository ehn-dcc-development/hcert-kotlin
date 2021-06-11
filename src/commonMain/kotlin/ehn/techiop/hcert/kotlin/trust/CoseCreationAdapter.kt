package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.PrivKey

expect class CoseCreationAdapter constructor(content: ByteArray) {

    fun addProtectedAttribute(key: CoseHeaderKeys, value: Any)

    fun addUnprotectedAttribute(key: CoseHeaderKeys, value: Any)

    fun sign(key: PrivKey)

    fun encode(): ByteArray

}