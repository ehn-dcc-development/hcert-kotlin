package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.PrivKey

expect class CoseCreationAdapter constructor(content: ByteArray) {
    fun addProtectedAttributeByteArray(key: Int, value: Any)
    fun sign(key: PrivKey<*>)
    fun encode(): ByteArray
}