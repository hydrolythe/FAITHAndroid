package be.hogent.faith.domain

class Emotion(val emotionType: EmotionType) {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun addDetail(detail: Detail) {
        _details += detail
    }
}