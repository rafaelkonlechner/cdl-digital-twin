package at.ac.tuwien.big

import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import org.junit.Test
import java.util.concurrent.TimeUnit

class InfluxTest {

    val dbName = "pick-and-place-test"
    var influxDB: InfluxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root")

    @Test
    fun test() {
        println(influxDB.databaseExists(dbName))
        val query = Query("SELECT base FROM actuators", dbName)
        val result = influxDB.query(query)
        for (r in result.results) {
            print(r)
        }
    }
}
