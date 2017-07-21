package at.ac.tuwien.big.entity

import java.time.LocalDateTime

interface LogEntry : Entity {

    val timeStamp: LocalDateTime
}
