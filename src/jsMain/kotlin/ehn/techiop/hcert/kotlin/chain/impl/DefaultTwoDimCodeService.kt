package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.TwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.toUint8ClampedArray
import qrcode.Decoder
import qrcode.Encoder
import qrcode.ErrorCorrectionLevel
import kotlin.math.roundToInt
import kotlin.math.sqrt

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
     * Decodes the content of an image of a 2D code.
     *
     * Note: This method can not decode the pictures generated with [encode].
     * The JS library seems to expect the rgb-encoded data of an image ...
     */
    override fun decode(input: ByteArray): String {
        val decoder = Decoder()
        // calculate width and height as expected by JS library
        val data = input.toUint8ClampedArray()
        val pixelCount = data.length / 4
        val width = sqrt(pixelCount.toFloat()).roundToInt()
        val result = decoder.decode(data, width, width)
        return result.data
    }

}