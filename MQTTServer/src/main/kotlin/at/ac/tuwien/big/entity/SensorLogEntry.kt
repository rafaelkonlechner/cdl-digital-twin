package at.ac.tuwien.big.entity

import java.time.LocalDateTime

data class SensorLogEntry(override val id: String, override val timeStamp: LocalDateTime, val message: String): LogEntry
