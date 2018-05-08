package at.ac.tuwien.big

import at.ac.tuwien.big.api.data.TrackingResult
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Service for tracking objects in images
 */
class ObjectTracker(private val host: String) {

    private val gson = Gson()

    /**
     * Track white objects in an image and receive bounding rectangle coordinates
     * @param imageFile the image
     * @param callback function called when result is ready
     * @return  [response] includes the bounding box (x,y)-coordinates, as well as
     * width and height for each identified object in a 200x200 normalized image.
     */
    fun track(imageFile: File, callback: (List<TrackingResult>) -> Unit) {

        Fuel.upload("http://$host:3000/analyze").dataParts { _, _ ->
            listOf(DataPart(imageFile, "image"))
        }.responseString { _, response, _ ->
                    val turnsType = object : TypeToken<List<TrackingResult>>() {}.type
                    callback(gson.fromJson(String(response.data), turnsType))
                }
    }
}
