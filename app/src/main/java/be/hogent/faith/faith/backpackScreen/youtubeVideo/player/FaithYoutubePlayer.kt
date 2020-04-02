package be.hogent.faith.faith.backpackScreen.youtubeVideo.player

import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class FaithYoutubePlayer(
    val youtubeVideoDetail: YoutubeVideoDetail,
    val youtubePlayerView : YouTubePlayerView,
    val playerParentView : View,
    val playButton: View,
    val pauseButton: View,
    var isFullscreen : Boolean = false
) {

    var seekBar : SeekBar? = null
    var currentTimeField : TextView? = null
    var durationField : TextView? = null
    var stopButton : View? = null
    var fullscreenButton : View? = null

    fun hasSeekbar() : Boolean{
        return seekBar!=null
    }

    fun hasCurrentTimeField() : Boolean{
        return currentTimeField != null
    }

    fun hasDurationField() : Boolean{
        return durationField != null
    }

    fun hasStopButton() : Boolean{
        return stopButton != null
    }

    fun hasFullScreenButton() : Boolean{
        return fullscreenButton !=null
    }

}