package at.ac.tuwien.big

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit4.SpringRunner
import sun.misc.BASE64Encoder

@RunWith(SpringRunner::class)
@SpringBootTest
class MqttServerApplicationTests {

    @Test
    fun contextLoads() {
    }

    @Test
    fun testAnalyzeImage() {
        analyzeImage(ClassPathResource("images/test.png").file)
    }
}
