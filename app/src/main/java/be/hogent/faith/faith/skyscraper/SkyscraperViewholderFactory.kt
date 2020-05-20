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

object SkyscraperViewholderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkyscraperViewHolder {
        /*val thumbnailView = LayoutInflater.from(parent.context).inflate(
            R.layout.detail_item_rv,
            parent,
            false
        ) as LinearLayout*/

        return when (viewType) {
            SkyscraperColors.SKYSCRAPER_BLUE -> createBlueViewHolder(parent)
            SkyscraperColors.SKYSCRAPER_DARK_GREEN -> createDarkGreenViewHolder(parent)

            else -> createBlueViewHolder(parent)
        }
    }

    private fun createBlueViewHolder(parent: ViewGroup): SkyscraperViewHolder.BlueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.skyscraper_rv_blue,
            parent,
            false
        ) as ConstraintLayout
        return SkyscraperViewHolder.BlueViewHolder(view)
    }
    private fun createDarkGreenViewHolder(parent: ViewGroup): SkyscraperViewHolder.DarkGreenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.skyscraper_rv_dark_green,
            parent,
            false
        ) as ConstraintLayout
        return SkyscraperViewHolder.DarkGreenViewHolder(view)
    }

    /*private fun createTextDetailViewHolder(
        thumbnailView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ): TextDetailViewHolder {
        thumbnailView.background = null
        return TextDetailViewHolder(thumbnailView, existingDetailNavigationListener)
    }*/
}

sealed class SkyscraperViewHolder(
    val view: ConstraintLayout
) : RecyclerView.ViewHolder(view), KoinComponent {


    fun bind(skyscraper: Skyscraper) {
        view.txt_goal_description.text =
            if (skyscraper.description.length < 60) skyscraper.description else "${skyscraper.description.substring(
                0,
                60
            )}..."
    }

    class BlueViewHolder(
        view: ConstraintLayout
    ) : SkyscraperViewHolder(view) {

    }
    class DarkGreenViewHolder(
        view: ConstraintLayout
    ) : SkyscraperViewHolder(view) {

    }

    /*class PictureDetailViewHolder(
        imageView: LinearLayout,
        existingDetailNavigationListener: ExistingDetailNavigationListener
    ) : DetailViewHolder(imageView, existingDetailNavigationListener) {

        override fun load(detail: Detail): RequestBuilder<Drawable> {
            if (detail.thumbnail == null)
                return Glide.with(thumbnailView).load(if (detail is PhotoDetail) R.drawable.ic_camera else R.drawable.ic_tekenen)
            return Glide.with(thumbnailView)
                .load(Base64.decode(detail.thumbnail, Base64.DEFAULT))
        }
    }*/
}