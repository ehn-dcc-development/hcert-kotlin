package ehn.techiop.hcert.kotlin.chain.common

const val oidTest = "1.3.6.1.4.1.0.1847.2021.1.1"
const val oidVaccination = "1.3.6.1.4.1.0.1847.2021.1.2"
const val oidRecovery = "1.3.6.1.4.1.0.1847.2021.1.3"

expect object PkiUtils {
    fun calcKid(encodedCert: ByteArray): ByteArray
}