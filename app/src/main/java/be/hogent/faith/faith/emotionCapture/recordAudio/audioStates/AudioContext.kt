package be.hogent.faith.faith.emotionCapture.recordAudio.audioStates

interface AudioContext {
    fun goToNextState(audioState: AudioState)
}