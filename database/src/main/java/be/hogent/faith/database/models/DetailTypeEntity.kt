package be.hogent.faith.database.models

/**
 * DetailTypeEntity requires an ID  so it can be mapped to a column in Room.
 */
enum class DetailTypeEntity(val id: Int) {
    TEXT(0),
    PICTURE(1),
    MUSIC(2),
    AUDIO(3),
    DRAWING(4),
    VIDEO(5)
}
