package be.hogent.faith.domain

class EmotionDiary {

    private val _emotions = mutableListOf<Emotion>()
    val emotions: List<Emotion>
        get() = _emotions

    fun addEmotion(emotion: Emotion) {
        _emotions += emotion
    }
}