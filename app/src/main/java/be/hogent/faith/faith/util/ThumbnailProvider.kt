package be.hogent.faith.faith.util

/**
 * returns the url to the default quality image fo a YouTube video
 * */
fun getDefaultThumbnailUrl(videoId : String) : String {
    return "https://i.ytimg.com/vi/$videoId/default.jpg"
}

fun getHighQualityThumbnailUrl(videoId: String) : String{
    return "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
}