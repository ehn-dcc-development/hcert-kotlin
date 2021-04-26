package ehn.techiop.hcert.kotlin.chain.impl

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
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
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.fromBase64
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class DefaultTwoDimCodeService(private val size: Int, private val format: BarcodeFormat = BarcodeFormat.QR_CODE) :
    TwoDimCodeService {

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
     * Generates a 2D code, returns the image itself as Base64 encoded string
     */
    override fun encode(data: String): String {
        try {
            val encoded = when (writer) {
                is QRCodeWriter -> {
                    val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q)
                    writer.encode(data, format, size, size, hints)
                }
                is AztecWriter -> {
                    writer.encode(data, format, size, size)
                }
                else -> throw IllegalArgumentException("format")
            }
            val bufferedImage = MatrixToImageWriter.toBufferedImage(encoded)
            val bout = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "png", bout)
            return bout.toByteArray().asBase64()
        } catch (e: Exception) {
            throw IllegalArgumentException("Cannot create 2D code", e)
        }
    }

    /**
     * Decodes the content of a Base64 encoded image of a 2D code
     */
    override fun decode(input: String): String {
        try {
            val bufferedImage = ImageIO.read(input.fromBase64().inputStream())
            val source = BufferedImageLuminanceSource(bufferedImage)
            val binarizer = HybridBinarizer(source)
            val bitmap = BinaryBitmap(binarizer)
            return reader.decode(bitmap).text
        } catch (e: Exception) {
            throw IllegalArgumentException("Cannot read 2D code", e)
        }
    }

}