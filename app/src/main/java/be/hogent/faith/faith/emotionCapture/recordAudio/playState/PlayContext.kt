package be.hogent.faith.faith.emotionCapture.recordAudio.playState

interface PlayContext {
    fun goToPlayState(newState: PlayState)
}