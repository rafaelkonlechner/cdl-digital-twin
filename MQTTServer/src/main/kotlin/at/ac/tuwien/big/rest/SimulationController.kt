package at.ac.tuwien.big.rest

import at.ac.tuwien.big.MessageController
import at.ac.tuwien.big.TimeSeriesCollectionService
import org.springframework.web.bind.annotation.*

/**
 * Control aspects of the simulation via REST
 */
@RestController
@CrossOrigin
class SimulationController(val messageController: MessageController) {

    /**
     * @return whether automatic mode is running or not
     */
    @GetMapping("/autoPlay")
    fun autoPlay() = messageController.autoPlay

    /**
     * @return whether the server is recording sensor signals to the time series database or not
     */
    @GetMapping("/recording")
    fun record() = messageController.recording

    /**
     * Start and stop automatic mode
     */
    @RequestMapping(value = ["/autoPlay"], method = arrayOf(RequestMethod.PUT))
    fun setAutoPlay(@RequestBody autoPlay: Boolean) {
        messageController.autoPlay = autoPlay
    }

    /**
     * Start and stop recording sensor signals to the time series database
     */
    @RequestMapping(value = ["/recording"], method = arrayOf(RequestMethod.PUT))
    fun setRecording(@RequestBody recording: Boolean) {
        messageController.recording = recording
    }

    /**
     * Reset the time series database
     */
    @RequestMapping(value = ["/resetData"], method = arrayOf(RequestMethod.PUT))
    fun resetData() {
        TimeSeriesCollectionService.resetDatabase()
    }
}
