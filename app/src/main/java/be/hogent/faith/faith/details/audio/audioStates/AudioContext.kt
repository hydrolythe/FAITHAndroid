package be.hogent.faith.faith.details.audio.audioStates

interface AudioContext {
    fun goToNextState(audioState: AudioState)
    var finishedRecording: Boolean
}