package ehn.techiop.hcert.kotlin.chain.impl

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.aztec.AztecReader
import com.google.zxing.aztec.AztecWriter
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import ehn.techiop.hcert.kotlin.chain.TwoDimCodeService
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Encodes the input as an 2D code.
 *
 * Output depends on [size] (in pixels), [marginSize] (in "units" acc. to ZXing implementation),
 * and of course [format] (either [BarcodeFormat.AZTEC] or [BarcodeFormat.QR_CODE]).
 */
class DefaultTwoDimCodeService @JvmOverloads constructor(
    private val size: Int,
    private val format: BarcodeFormat = BarcodeFormat.QR_CODE,
    private val marginSize: Int = 2
) : TwoDimCodeService {

    private val writer = when (format) {
        BarcodeFormat.QR_CODE -> QRCodeWriter()
        BarcodeFormat.AZTEC -> AztecWriter()
        else -> throw IllegalArgumentException("format")
    }

    private val reader = when (format) {
        BarcodeFormat.QR_CODE -> QRCodeReader()
        BarcodeFormat.AZTEC -> AztecReader()
        else -> throw IllegalArgumentException("format")
    }

    /**
     * Generates a 2D code, returns the image itself as an encoded png
     */
    override fun encode(data: String): ByteArray {
        val encoded = when (writer) {
            is QRCodeWriter -> {
                val hints = mapOf(
                    EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q,
                    EncodeHintType.MARGIN to marginSize
                )
                writer.encode(data, format, size, size, hints)
            }
            is AztecWriter -> {
                // For Aztec, ErrorCorrection would be in percent, Margin is not relevant
                writer.encode(data, format, size, size)
            }
            else -> throw IllegalArgumentException("format")
        }
        val bufferedImage = MatrixToImageWriter.toBufferedImage(encoded)
        val bout = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", bout)
        return bout.toByteArray()
    }

    /**
     * Decodes the content of a png encoded image of a 2D code
     */
    override fun decode(input: ByteArray): String {
        val bufferedImage = ImageIO.read(input.inputStream())
        val source = BufferedImageLuminanceSource(bufferedImage)
        val binarizer = HybridBinarizer(source)
        val bitmap = BinaryBitmap(binarizer)
        val hints = mapOf(
            DecodeHintType.PURE_BARCODE to true,
            DecodeHintType.POSSIBLE_FORMATS to listOf(format)
        )
        return reader.decode(bitmap, hints).text
    }

}