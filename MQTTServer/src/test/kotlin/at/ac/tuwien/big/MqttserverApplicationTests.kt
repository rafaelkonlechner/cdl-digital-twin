package at.ac.tuwien.big

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class MqttServerApplicationTests {

    @Test
    fun contextLoads() {
    }

    @Test
    fun testAnalyzeImage() {
        CameraSignal.analyzeImage(ClassPathResource("images/test.png").file, { x -> println(x) })
    }
}
