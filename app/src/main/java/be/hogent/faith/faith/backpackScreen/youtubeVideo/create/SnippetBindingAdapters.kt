package be.hogent.faith.faith.backpackScreen.youtubeVideo.create

import android.widget.TextView
import androidx.databinding.BindingAdapter
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail

@BindingAdapter("snippet_title")
fun TextView.setSnippetTitle(item: YoutubeVideoDetail?) {
    item?.let {
        text = item.fileName
    }
}