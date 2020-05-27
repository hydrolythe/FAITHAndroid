package be.hogent.faith.domain.models.goals

import org.threeten.bp.LocalDateTime
import java.util.UUID

class Goal(
    val uuid: UUID = UUID.randomUUID(),
    var description: String,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var isCompleted: Boolean = false
)