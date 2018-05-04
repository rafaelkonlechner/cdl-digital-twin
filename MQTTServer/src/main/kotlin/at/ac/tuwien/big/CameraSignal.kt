package at.ac.tuwien.big

import at.ac.tuwien.big.rest.data.TrackingResult
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sun.misc.BASE64Decoder
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO

/**
 * Service for handling the camera signals
 */
object CameraSignal {

    private val gson = Gson()

    /**
     * Save an encoded image string to a file in the *png* file format.
     */
    fun saveImage(base64Image: String) {
        val imageBytes = BASE64Decoder().decodeBuffer(base64Image)
        val inputStream = ByteArrayInputStream(imageBytes)
        val image = ImageIO.read(inputStream)
        inputStream.close()
        ImageIO.write(image, "png", File("test-qc-${LocalDateTime.now()}.png"))
    }

    /**
     * Encodes a given image in the *png* file format into Base64.
     * @return the encoded image in Base64
     */
    fun toBase64(img: ByteArray) = String(Base64.getEncoder().encode(img))

    fun fromBase64(img: String) = Base64.getDecoder().decode(img)!!

    /**
     * Sends a camera image to the tracking service. *Note:* This service has not yet been implemented
     */
    fun analyzeImage(imageFile: File, callback: (List<TrackingResult>) -> Unit) {

        Fuel.upload("http://localhost:3000/analyze").dataParts { _, _ ->
            listOf(DataPart(imageFile, "image"))
        }.responseString { _, response, _ ->
                    val turnsType = object : TypeToken<List<TrackingResult>>() {}.type
                    callback(gson.fromJson(String(response.data), turnsType))
                }
    }
}
