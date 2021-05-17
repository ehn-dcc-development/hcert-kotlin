package ehn.techiop.hcert.kotlin.chain.common

actual object PkiUtils {

    actual fun calcKid(encodedCert: ByteArray): ByteArray {
        // TODO KID calculation in JS
        return js("sha256()")
    }

}