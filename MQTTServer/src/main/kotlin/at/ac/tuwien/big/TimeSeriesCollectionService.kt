package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.ConveyorState
import at.ac.tuwien.big.entity.state.GatePassed
import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.state.TestingRigState
import org.influxdb.InfluxDB
import org.influxdb.InfluxDB.ConsistencyLevel
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.BatchPoints
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

object TimeSeriesCollectionService {

    val dbName = "aTimeSeries"
    var influxDB: InfluxDB = InfluxDBFactory.connect("http://127.0.0.1:8086", "root", "root")

    init {
        influxDB.createDatabase(dbName)
        influxDB.enableBatch(100, 100, TimeUnit.MILLISECONDS)
    }

    fun resetDatabase() {
        influxDB.deleteDatabase(dbName)
        influxDB.createDatabase(dbName)
    }

    fun savePoint(state: GatePassed) {
        val batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("gate")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("channel", state.channel)
                .build()
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }

    fun savePoint(state: ConveyorState) {
        val batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
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

    fun savePoint(state: TestingRigState) {
        val batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
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

    fun savePoint(state: RoboticArmState) {
        val batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
                .consistency(ConsistencyLevel.ALL)
                .build()
        val point = Point.measurement("actuators")
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
}
