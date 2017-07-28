package at.ac.tuwien.big

import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin
class StateController(val messageController: MessageController) {

    @GetMapping("/all")
    fun findAll() = States.all

    @GetMapping("/autoPlay")
    fun autoPlay() = messageController.autoPlay

    @RequestMapping(value = "/autoPlay", method = arrayOf(RequestMethod.PUT))
    fun setAutoPlay(@RequestBody autoPlay: Boolean) {
        messageController.autoPlay = autoPlay
    }
}
