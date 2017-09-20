package at.ac.tuwien.big

import at.ac.tuwien.big.entity.message.ProductCode
import org.apache.commons.io.FileUtils
import org.junit.Test
import java.io.File
import java.util.*

class QRCodeReaderTest {

    @Test
    fun generateQRCodes() {

        val random = Random()
        for (i in 1..100) {
            val uuid = UUID.randomUUID().toString()
            val id = uuid.substring(32..35)
            val batch = when {
                i < 50 -> "1"
                else -> "2"
            }
            val color = if (random.nextBoolean()) "red" else "green"
            val code = ProductCode(id, batch, color, null)
            val byteArray = QRCodeReader.writeText(code)
            FileUtils.writeByteArrayToFile(File("../qr-codes/code-$i.png"), byteArray)
        }
    }
}
