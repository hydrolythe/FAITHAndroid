package be.hogent.faith.faith.videoplayer

/**
 * Creates a label like this: 2:45
 */
fun createTimeLabel(time: Float): String {
    val min: Int = time.toInt() / 60
    val sec: Int = time.toInt() % 60

    var timeLabel = min.toString()
    timeLabel += ":"
    if (sec < 10)
        timeLabel += "0"
    timeLabel += sec
    return timeLabel
}