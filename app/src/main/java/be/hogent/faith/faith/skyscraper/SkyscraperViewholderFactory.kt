package be.hogent.faith.faith.skyscraper

import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.AudioDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.ExistingDetailNavigationListener
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.VideoDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.PictureDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder.TextDetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.Skyscraper
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SkyscraperColors
import be.hogent.faith.faith.util.getDefaultThumbnailUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import kotlinx.android.synthetic.main.detail_item_rv.view.btn_delete_detailRv
import kotlinx.android.synthetic.main.detail_item_rv.view.detail_img
import kotlinx.android.synthetic.main.detail_item_rv.view.text_detail_title
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.txt_goal_description
import org.koin.core.KoinComponent

object SkyscraperViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkyscraperViewHolder {

        val view: ConstraintLayout
        return when (viewType) {
            SkyscraperColors.SKYSCRAPER_BLUE -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_blue,
                    parent,
                    false
                ) as ConstraintLayout
                SkyscraperViewHolder(view)
            }
            SkyscraperColors.SKYSCRAPER_YELLOW -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_yellow,
                    parent,
                    false
                ) as ConstraintLayout
                SkyscraperViewHolder(view)
            }
            SkyscraperColors.SKYSCRAPER_PINK -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_dark_green,
                    parent,
                    false
                ) as ConstraintLayout
                SkyscraperViewHolder(view)
            }
            SkyscraperColors.SKYSCRAPER_DARK_GREEN -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_dark_green,
                    parent,
                    false
                ) as ConstraintLayout
                SkyscraperViewHolder(view)
            }
            SkyscraperColors.SKYSCRAPER_GREEN -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_green,
                    parent,
                    false
                ) as ConstraintLayout
                SkyscraperViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.skyscraper_rv_blue,
                    parent,
                    false
                ) as ConstraintLayout
                SkyscraperViewHolder(view)
            }
        }
    }
}

class SkyscraperViewHolder(
    val view: ConstraintLayout
) : RecyclerView.ViewHolder(view), KoinComponent {


    fun bind(skyscraper: Skyscraper) {
        view.txt_goal_description.text =
            if (skyscraper.description.length < 60) skyscraper.description else "${skyscraper.description.substring(
                0,
                60
            )}..."
    }
}