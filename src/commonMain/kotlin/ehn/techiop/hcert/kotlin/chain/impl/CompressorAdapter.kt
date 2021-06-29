package ehn.techiop.hcert.kotlin.chain.impl

/**
 * Can't put this into an expect class
 * Work around https://youtrack.jetbrains.com/issue/KT-21186
 */
object CompressionConstants {
    /**
     * Limit the byte array size after decompression to 5 MB.
     *
     * Reasoning:
     * 1. QR codes can hold at most < 4500 alphanumeric chars (https://www.qrcode.com/en/about/version.html)
     *    Sidenote: The EHN spec recommends a compression level of Q, which limits it to at most < 2500 alphanumeric chars
     * 	  (https://ec.europa.eu/health/sites/default/files/ehealth/docs/digital-green-certificates_v1_en.pdf#page=7)
     *    This is a lower bound (since any DCC should be encodable in both Aztec and QR codes).
     * 2. As an additional upper bound: base45 encodes 2 bytes into 3 chars (https://datatracker.ietf.org/doc/html/draft-faltstrom-base45-04#section-4)
     * 3.  zlib's maximum compression factor is roughly 1000:1 (http://www.zlib.net/zlib_tech.html)
     */
    const val MAX_DECOMPRESSED_SIZE = 5 * 1024 * 1024
}

expect class CompressorAdapter() {

    fun encode(input: ByteArray, level: Int): ByteArray

    fun decode(input: ByteArray): ByteArray

}