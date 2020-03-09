package be.hogent.faith.storage.backpack

data class DummyDetail(
    var file: String = "",
    val uuid: String,
    val type: String
)

enum class DetailType {
    TEXT, AUDIO, DRAWING, PHOTO
}