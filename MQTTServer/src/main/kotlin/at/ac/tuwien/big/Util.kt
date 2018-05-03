package at.ac.tuwien.big

object Util {

    fun interpolate(from: Double, to: Double, time: Int): List<Pair<Int, Double>> {
        val linearDiff = (to - from) / time
        return (1..time step 10).map {
            Pair(it, from + (linearDiff * it))
        }
    }

    fun getPosition(x1: Double, x2: Double, duration: Long, time: Long): Double {
        return if (duration > 0.0) {
            x1 + ((x2 - x1) / duration.toDouble() * time.toDouble())
        } else {
            x2
        }
    }

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

}