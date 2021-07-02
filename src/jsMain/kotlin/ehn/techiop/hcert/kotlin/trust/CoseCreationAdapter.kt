package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Cose
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import kotlin.js.json

actual class CoseCreationAdapter actual constructor(private val content: ByteArray) {

    private val protectedHeader = json()
    private val unprotectedHeader = json()
    private var encoded: ByteArray = byteArrayOf()

    actual fun addProtectedAttribute(key: CoseHeaderKeys, value: Any) {
        val content = if (value is CwtAlgorithm) value.stringVal else value
        protectedHeader.set(key.stringVal, content)
    }

    actual fun addUnprotectedAttribute(key: CoseHeaderKeys, value: Any) {
        val content = if (value is CwtAlgorithm) value.stringVal else value
        unprotectedHeader.set(key.stringVal, content)
    }

    actual fun sign(key: PrivKey) {
        val header = json("p" to protectedHeader, "u" to unprotectedHeader)
        encoded = Cose.sign(header, content, key).toByteArray()
    }

    actual fun encode(): ByteArray {
        return encoded
    }

}