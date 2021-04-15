package ehn.techiop.hcert.kotlin.chain.impl

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.aztec.AztecWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import ehn.techiop.hcert.kotlin.chain.TwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.asBase64
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class DefaultTwoDimCodeService(private val size: Int, private val format: BarcodeFormat) : TwoDimCodeService {

    private val writer = when (format) {
        BarcodeFormat.QR_CODE -> QRCodeWriter()
        BarcodeFormat.AZTEC -> AztecWriter()
        else -> throw IllegalArgumentException("format")
    }

    /**
     * Generates a 2D code, returns the image itself as Base64 encoded string
     */
    override fun encode(data: String): String {
        try {
            val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q)
            val encoded = writer.encode(data, format, size, size, hints)
            val bufferedImage = MatrixToImageWriter.toBufferedImage(encoded)
            val bout = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "png", bout)
            return bout.toByteArray().asBase64()
        } catch (e: Exception) {
            throw IllegalArgumentException("Cannot create 2D code", e)
        }
    }

}