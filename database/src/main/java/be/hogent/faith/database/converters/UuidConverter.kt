package be.hogent.faith.database.converters

import java.util.UUID

class UuidConverter {

    fun toString(uuid: UUID): String {
        return uuid.toString()
    }

    fun toUuid(uuidString: String): UUID {
        return UUID.fromString(uuidString)
    }
}