package at.ac.tuwien.big.entity.log

import at.ac.tuwien.big.entity.Entity
import java.time.LocalDateTime

interface LogEntry : Entity {

    val timeStamp: LocalDateTime
}
