package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.StateEvent
import java.util.*

/**
 * Check similarity of floating point numbers with a precision of 0.2
 */
val singleAccuracy = { x: Double, y: Double -> similar(x, y, 0.2) }

/**
 * Check similarity of floating point numbers with a precision of 0.02
 */
val doubleAccuracy = { x: Double, y: Double -> similar(x, y, 0.03) }

/**
 * Equality check with an epsilon of 0.02
 * @return true, if the distance between the two real numbers is < 0.02 and false otherwise
 */
fun similar(a: Double, b: Double, accuracy: Double) = Math.abs(a - b) <= accuracy

/**
 * Encode a given image in the *png* file format in Base64.
 * @return the Base64 encoded image
 */
fun toBase64(img: ByteArray) = String(Base64.getEncoder().encode(img))

/**
 * Decode a Base64 String into the according *png* image
 * @return the decoded image in *png*
 */
fun fromBase64(img: String) = Base64.getDecoder().decode(img)!!

/**
 * Help building maps from state name to state
 */
fun <T : StateEvent> createMap(vararg states: T) = states.map { Pair(it.name, it) }.toMap()

/**
 * Get the point on the x-position [time] on the interpolated straight: (0,[y1]) (duration,[y2]).
 */
fun getPosition(y1: Double, y2: Double, duration: Long, time: Long): Double {
    return if (duration > 0.0) {
        y1 + ((y2 - y1) / duration.toDouble() * time.toDouble())
    } else {
        y2
    }
}

/**
 * Compute the time a linear actuator takes from [start][from] to [finish][to] in milliseconds.
 */
fun timeToTarget(from: Double, to: Double): Long {
    val radPerMillisecond = (0.00872665 * 60) / 1000
    val dist = distance(from, to)
    return (dist / radPerMillisecond).toLong()
}

/**
 * Compute the time an accelerated actuator takes from [start][from] to [finish][to] in milliseconds.
 */
fun timeToTarget(from: Double, to: Double, acceleration: Double, maxSpeed: Double): Long {
    val target = distance(from, to)
    var speed = 0.0
    var dist = 0.0
    var frames = 0
    while (dist < target) {
        if (breakDistance(speed, acceleration) > target - dist) {
            speed -= acceleration
            dist += speed
        } else {
            speed = Math.min(speed + acceleration, maxSpeed)
            dist += speed
        }
        frames++
    }
    return ((frames.toDouble() / 60) * 1000).toLong()
}

private fun distance(position: Double, target: Double): Double {
    val p = position + Math.PI
    val t = target + Math.PI
    return Math.min(Math.abs(p - t), Math.PI * 2 - Math.max(p, t) + Math.min(p, t))
}

private fun breakDistance(speed: Double, deceleration: Double): Double {
    var tmp = speed
    var dist = 0.0
    while (tmp > 0) {
        dist += tmp
        tmp -= deceleration
    }
    return dist
}