package at.ac.tuwien.big

import sun.misc.BASE64Decoder
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO

fun saveImage(base64Image: String) {
    val imageBytes = BASE64Decoder().decodeBuffer(base64Image)
    val inputStream = ByteArrayInputStream(imageBytes)
    val image = ImageIO.read(inputStream)
    inputStream.close()
    ImageIO.write(image, "png", File("test-qc-${LocalDateTime.now()}.png"))
}
