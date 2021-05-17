package ehn.techiop.hcert.kotlin.chain.common

import Hash
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array

actual object PkiUtils {

    actual fun calcKid(encodedCert: ByteArray): ByteArray {
        val hash = Hash()
        hash.update(encodedCert.toUint8Array())
        return hash.digest().toByteArray().copyOf(8)
    }

}
