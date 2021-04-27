package ehn.techiop.hcert.kotlin.chain

interface TwoDimCodeService {

    /**
     * Generates a 2D code, returns the image itself as an encoded png
     */
    fun encode(data: String): ByteArray

    /**
     * Decodes the content of a png encoded image of a 2D code
     */
    fun decode(input: ByteArray): String

}