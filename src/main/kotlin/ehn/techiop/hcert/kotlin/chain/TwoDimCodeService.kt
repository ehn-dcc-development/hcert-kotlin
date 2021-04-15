package ehn.techiop.hcert.kotlin.chain

interface TwoDimCodeService {

    /**
     * Generates a 2D code, returns the image itself as Base64 encoded string
     */
    fun encode(data: String): String

}