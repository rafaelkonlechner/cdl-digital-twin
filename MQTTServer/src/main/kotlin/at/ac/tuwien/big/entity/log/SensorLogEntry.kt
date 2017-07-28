package at.ac.tuwien.big.entity.log

import java.time.LocalDateTime

data class SensorLogEntry(override val id: String, override val timeStamp: LocalDateTime, val message: String): LogEntry
