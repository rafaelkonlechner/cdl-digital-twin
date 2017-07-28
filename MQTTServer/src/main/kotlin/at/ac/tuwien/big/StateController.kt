package at.ac.tuwien.big

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin
class StateController {

    @GetMapping("/all")
    fun findAll() = States.all
}
