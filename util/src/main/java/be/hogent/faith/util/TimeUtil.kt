package be.hogent.faith.util

fun toMinutesSecondString(durationInSeconds: Long): String {
    val minutesPadded = (durationInSeconds / 60).toString().padStart(2, '0')
    val secondsPadded = (durationInSeconds % 60).toString().padStart(2, '0')
    return "$minutesPadded:$secondsPadded"
}