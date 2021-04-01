package be.hogent.faith.faith.details.youtubeVideo.create

import android.widget.TextView
import androidx.databinding.BindingAdapter
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail

@BindingAdapter("snippet_title")
fun TextView.setSnippetTitle(item: YoutubeVideoDetail?) {
    item?.let {
        text = item.title
    }
}