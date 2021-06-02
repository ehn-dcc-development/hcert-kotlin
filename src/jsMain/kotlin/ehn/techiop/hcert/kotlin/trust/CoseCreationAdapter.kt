package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Cose
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import kotlin.js.json

actual class CoseCreationAdapter actual constructor(private val content: ByteArray) {

    private val header = json()
    private var encoded: ByteArray = byteArrayOf()

    actual fun addProtectedAttributeByteArray(key: Int, value: Any) {
        header.set(key.toString(), value)
    }

    actual fun sign(key: PrivKey<*>) {
        encoded = Cose.sign(header, content, key).toByteArray()
    }

    actual fun encode(): ByteArray {
        return encoded
    }

    //    suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    //        then({ cont.resume(it) }, { cont.resumeWithException(it) })
    //    }

}