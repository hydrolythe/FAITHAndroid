package be.hogent.faith.faith.cinema

import android.widget.TextView
import androidx.databinding.BindingAdapter
import be.hogent.faith.faith.models.detail.Detail

@BindingAdapter("selected_detail_title")
fun TextView.setSelectedDetailTitle(item: Detail?) {
    item?.let {
        text = item.title
    }
}