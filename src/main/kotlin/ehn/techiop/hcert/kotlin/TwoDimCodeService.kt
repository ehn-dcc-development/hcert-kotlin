package ehn.techiop.hcert.kotlin

import com.google.zxing.BarcodeFormat
import com.google.zxing.aztec.AztecWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class TwoDimCodeService(private val size: Int, private val format: BarcodeFormat) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val writer = when (format) {
        BarcodeFormat.QR_CODE -> QRCodeWriter()
        BarcodeFormat.AZTEC -> AztecWriter()
        else -> throw IllegalArgumentException("format")
    }

    /**
     * Generates a 2D code, returns the image itself as Base64 encoded string
     */
    fun encode(data: String): String {
        try {
            val encoded = writer.encode(data, format, size, size)
            val bufferedImage = MatrixToImageWriter.toBufferedImage(encoded)
            val bout = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "png", bout)
            return bout.toByteArray().asBase64()
        } catch (e: Exception) {
            logger.error("Cannot create 2D code", e)
            return "ERROR"
        }
    }

}