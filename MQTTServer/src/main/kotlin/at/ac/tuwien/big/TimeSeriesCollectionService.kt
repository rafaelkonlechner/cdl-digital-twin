package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
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

    val dbName = "pick-and-place"
    var influxDB: InfluxDB = InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root")

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

    /**
     * Saves the event of an item passing through a gate
     */
    fun savePoint(state: GatePassed) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("gate")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("channel", state.channel)
                .build()
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    /**
     * Saves the state of the conveyor
     */
    fun savePoint(state: ConveyorState) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("conveyor")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("adjuster", state.adjusterPosition)
                .addField("detected", state.detected)
                .addField("in_pickup_window", state.inPickupWindow)
                .build()
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    /**
     * Saves the state of the testing rig
     */
    fun savePoint(state: TestingRigState) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("testing_rig")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("platform", state.platformPosition)
                .addField("category", state.objectCategory.name)
                .build()
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    /**
     * Saves the state of the robotic arm
     */
    fun savePoint(state: RoboticArmState) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("robotic_arm")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("base", state.basePosition)
                .addField("main_arm", state.mainArmPosition)
                .addField("second_arm", state.secondArmPosition)
                .addField("wrist", state.wristPosition)
                .addField("gripper", state.gripperPosition)
                .build()
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
        val result = influxDB.query(query)
        return result
    }
}
