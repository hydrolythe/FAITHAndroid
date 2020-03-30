package be.hogent.faith.domain.models

import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

class Film(
    var title: String,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var file: File,
    val uuid: UUID = UUID.randomUUID()
)