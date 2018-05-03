package at.ac.tuwien.big

import at.ac.tuwien.big.entity.message.ProductCode
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import sun.misc.BASE64Decoder
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * Service for handling QR codes
 */
object QRCode {

    /**
     * Reads a QR code and creates a textual code representation
     * @param base64Image the encoded image
     * @return the code in [ProductCode], or [null] if an error occurred
     */
    fun read(base64Image: String): ProductCode? {

        val imageBytes = BASE64Decoder().decodeBuffer(base64Image)
        val inputStream = ByteArrayInputStream(imageBytes)
        val image = ImageIO.read(inputStream)
        val pixels = image.getRGB(0, 0, image.width, image.height, null, 0, image.width)
        val source = RGBLuminanceSource(image.width, image.height, pixels)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        try {
            val result: Result = reader.decode(bitmap)
            val text = result.text.split(",")
            if (text.size == 3) {
                val code = ProductCode(text[0], text[1], text[2], null)
                code.base64 = CameraSignal.toBase64(write(code))
                return code
            } else {
                return null
            }
        } catch (e: NotFoundException) {
            return null
        }
    }

    /**
     * Encodes the given object into a QR code and creates an image
     * @param code the product code
     * @return a byte array, representing the QR code image in the *png* file format
     */
    fun write(code: ProductCode): ByteArray {

        val hintMap = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hintMap[EncodeHintType.MARGIN] = 1
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

        val writer = QRCodeWriter()
        val text = "${code.id},${code.batchId},${code.color}"
        val matrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400, hintMap)
        val image = BufferedImage(matrix.width, matrix.height, BufferedImage.TYPE_INT_RGB)
        image.createGraphics()
        val graphics = image.graphics as Graphics2D
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, image.width, image.height)
        graphics.color = Color.BLACK

        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                if (matrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1)
                }
            }
        }
        val bytes = ByteArrayOutputStream()
        ImageIO.write(image, "png", bytes)
        return bytes.toByteArray()
    }
}