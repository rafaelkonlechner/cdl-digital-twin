package at.ac.tuwien.big.repository

import at.ac.tuwien.big.entity.SensorLogEntry
import org.springframework.data.mongodb.repository.MongoRepository

interface SensorLogEntryRepository : MongoRepository<SensorLogEntry, String> {
    fun findByMessage(Message: String): SensorLogEntry?
}