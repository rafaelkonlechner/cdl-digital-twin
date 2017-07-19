package at.ac.tuwien.big

import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import sun.misc.BASE64Decoder
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import org.springframework.core.io.FileSystemResource
import org.springframework.http.converter.ByteArrayHttpMessageConverter

fun saveImage(base64Image: String) {
    val imageBytes = BASE64Decoder().decodeBuffer(base64Image)
    val inputStream = ByteArrayInputStream(imageBytes)
    val image = ImageIO.read(inputStream)
    inputStream.close()
    ImageIO.write(image, "png", File("test-qc-${LocalDateTime.now()}.png"))
}

fun analyzeImage(imageFile: File) {

    val restTemplate = RestTemplate()
    restTemplate.messageConverters.add(ByteArrayHttpMessageConverter())

    val params: MultiValueMap<String, Any> = LinkedMultiValueMap<String, Any>()
    params.add("file", FileSystemResource(imageFile))

    val headers = HttpHeaders()
    headers.contentType = MediaType.MULTIPART_FORM_DATA
    val requestEntity = HttpEntity<MultiValueMap<String, Any>>(params, headers)
    restTemplate.exchange("http://localhost:3000/analyze", HttpMethod.POST, requestEntity, String::class.java)
}
