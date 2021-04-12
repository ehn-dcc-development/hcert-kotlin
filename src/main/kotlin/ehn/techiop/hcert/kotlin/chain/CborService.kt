package ehn.techiop.hcert.kotlin.chain

interface CborService {

    fun sign(input: ByteArray): ByteArray

    fun verify(input: ByteArray): ByteArray

}