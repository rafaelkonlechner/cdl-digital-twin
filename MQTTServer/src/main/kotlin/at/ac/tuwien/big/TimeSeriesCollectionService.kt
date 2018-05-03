package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.transition.ConveyorTransition
import at.ac.tuwien.big.entity.transition.RoboticArmTransition
import at.ac.tuwien.big.entity.transition.TestingRigTransition
import at.ac.tuwien.big.entity.transition.Transition
import org.influxdb.InfluxDB
import org.influxdb.InfluxDB.ConsistencyLevel
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.BatchPoints
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import org.influxdb.dto.QueryResult
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Service for storing data points in a time series database
 */
object TimeSeriesCollectionService {

    private const val dbName = "pick-and-place"
    private var influxDB: InfluxDB = InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root")

    init {
        influxDB.createDatabase(dbName)
        influxDB.enableBatch(100, 100, TimeUnit.MILLISECONDS)
    }

    /**
     * Reset the database
     */
    fun resetDatabase() {
        influxDB.deleteDatabase(dbName)
        influxDB.createDatabase(dbName)
    }

    fun savePoint(state: RoboticArmState, ref: RoboticArmState?, label: String? = null) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val pointBuilder = Point.measurement("robotic_arm")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("base", state.basePosition)
                .addField("ref_base", ref?.basePosition)
                .addField("base_re", if (ref != null) Math.abs(state.basePosition - ref.basePosition) else 0.0)
                .addField("main_arm", state.mainArmPosition)
                .addField("ref_main_arm", ref?.mainArmPosition)
                .addField("main_arm_re", if (ref != null) Math.abs(state.mainArmPosition - ref.mainArmPosition) else 0.0)
                .addField("second_arm", state.secondArmPosition)
                .addField("ref_second_arm", ref?.secondArmPosition)
                .addField("second_arm_re", if (ref != null) Math.abs(state.secondArmPosition - ref.secondArmPosition) else 0.0)
                .addField("wrist", state.wristPosition)
                .addField("gripper", state.gripperPosition)
        val point = if (label != null) {
            pointBuilder.addField("label", label).build()
        } else {
            pointBuilder.build()
        }
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    /**
     * Saves the event of a state transition
     */
    fun savePoint(transition: Transition) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val tag = when (transition) {
            is RoboticArmTransition -> "robotic_arm"
            is ConveyorTransition -> "conveyor"
            is TestingRigTransition -> "testing_rig"
            else -> ""
        }
        val point = Point.measurement("transitions")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("unit", tag)
                .addField("state", transition.targetState.name)
                .addField("progress", 1)
                .build()
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    /**
     * Logs a successful production cycle, i.e. from picking up the item to dropping it
     */
    fun logSuccessfulProduction() {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("productions")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("count", 1)
                .build()
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    /**
     * Returns the number of successful productions
     * @param since the duration for the query window into the past, starting from now
     * @param groupBy the duration for grouping the aggregation values
     */
    fun getSuccessfulProductions(since: Duration, groupBy: Duration): QueryResult {
        val query = Query("SELECT SUM(count) FROM productions WHERE time > now() - ${since.toMinutes()}m GROUP BY time(${groupBy.toMinutes()}m)", dbName)
        return influxDB.query(query)
    }
}
