package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.TwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toUint8ClampedArray
import qrcode.Decoder
import qrcode.Encoder
import qrcode.ErrorCorrectionLevel

/**
 * Encodes the input as an 2D QR code.
 */
class DefaultTwoDimCodeService(
    private val moduleSize: Int = 3,
    private val marginSize: Int = 2
) : TwoDimCodeService {

    /**
     * Generates a 2D code, returns the image itself as an encoded *gif*
     */
    override fun encode(data: String): ByteArray {
        val encoder = Encoder()
        encoder.setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
        encoder.write(data)
        encoder.make()
        val dataUrl = encoder.toDataURL(moduleSize = moduleSize, margin = marginSize)
        return dataUrl.replace("data:image/gif;base64,", "").fromBase64()
    }

    /**
     * Decodes the content of a *gif* encoded image of a 2D code
     */
    override fun decode(input: ByteArray): String {
        val decoder = Decoder()
        val result = decoder.decode(input.toUint8ClampedArray(), moduleSize, moduleSize)
        return result.data
    }

}