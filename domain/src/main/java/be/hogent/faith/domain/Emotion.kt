package be.hogent.faith.domain

class Emotion(val emotionType: EmotionType) {

    val details = mutableListOf<Detail>()
}