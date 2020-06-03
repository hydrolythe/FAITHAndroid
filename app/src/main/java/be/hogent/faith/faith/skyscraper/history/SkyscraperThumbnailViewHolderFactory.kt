package be.hogent.faith.faith.skyscraper.history

import android.graphics.drawable.Drawable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.skyscraper.startscreen.Skyscraper
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import kotlinx.android.synthetic.main.detail_item_rv.view.btn_delete_detailRv
import kotlinx.android.synthetic.main.detail_item_rv.view.text_detail_title
import org.koin.core.KoinComponent
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailBlueViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperHistoryNavigationListener
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailDarkGreenViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailGreenViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailPinkViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailYellowViewHolder
import kotlinx.android.synthetic.main.fragment_skyscraper_history.view.btn_delete_skyscraper
import kotlinx.android.synthetic.main.skyscraper_thumbnail_item_rv.view.skyscraper_img
import kotlinx.android.synthetic.main.skyscraper_thumbnail_item_rv.view.text_skyscraper_description

object SkyscraperThumbnailViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ): SkyscraperThumbnailViewHolder {
        val thumbnailView = LayoutInflater.from(parent.context).inflate(
            R.layout.skyscraper_thumbnail_item_rv,
            parent,
            false
        ) as LinearLayout

        return when (viewType) {
            SkyscraperColors.SKYSCRAPER_BLUE.value -> createSkyscraperThumbnailBlueViewHolder(
                thumbnailView,
                skyscraperHistoryNavigationListener
            )
            SkyscraperColors.SKYSCRAPER_DARK_GREEN.value -> createSkyscraperThumbnailDarkGreenViewHolder(
                thumbnailView,
                skyscraperHistoryNavigationListener
            )
            SkyscraperColors.SKYSCRAPER_GREEN.value -> createSkyscraperThumbnailGreenViewHolder(
                thumbnailView,
                skyscraperHistoryNavigationListener
            )
            SkyscraperColors.SKYSCRAPER_PINK.value -> createSkyscraperThumbnailPinkViewHolder(
                thumbnailView,
                skyscraperHistoryNavigationListener
            )
            SkyscraperColors.SKYSCRAPER_YELLOW.value -> createSkyscraperThumbnailYellowViewHolder(
                thumbnailView,
                skyscraperHistoryNavigationListener
            )
            else -> createSkyscraperThumbnailBlueViewHolder(
                thumbnailView,
                skyscraperHistoryNavigationListener
            )
        }
    }

    private fun createSkyscraperThumbnailBlueViewHolder(
        thumbnailView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ): SkyscraperThumbnailBlueViewHolder {
        thumbnailView.background = null
        return SkyscraperThumbnailBlueViewHolder(thumbnailView, skyscraperHistoryNavigationListener)
    }

    private fun createSkyscraperThumbnailDarkGreenViewHolder(
        thumbnailView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ): SkyscraperThumbnailDarkGreenViewHolder {
        thumbnailView.background = null
        return SkyscraperThumbnailDarkGreenViewHolder(thumbnailView, skyscraperHistoryNavigationListener)
    }

    private fun createSkyscraperThumbnailGreenViewHolder(
        thumbnailView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ): SkyscraperThumbnailGreenViewHolder {
        return SkyscraperThumbnailGreenViewHolder(thumbnailView, skyscraperHistoryNavigationListener)
    }

    private fun createSkyscraperThumbnailPinkViewHolder(
        thumbnailView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ): SkyscraperThumbnailPinkViewHolder {
        return SkyscraperThumbnailPinkViewHolder(thumbnailView, skyscraperHistoryNavigationListener)
    }

    private fun createSkyscraperThumbnailYellowViewHolder(
        thumbnailView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ): SkyscraperThumbnailYellowViewHolder {
        return SkyscraperThumbnailYellowViewHolder(thumbnailView, skyscraperHistoryNavigationListener)
    }


}

sealed class SkyscraperThumbnailViewHolder(
    val thumbnailView: LinearLayout,
    private val skyscraperHistoryNavigationListener: SkyscraperThumbnailViewHolder.SkyscraperHistoryNavigationListener
) : RecyclerView.ViewHolder(thumbnailView), KoinComponent {

    // clicklisteners on separate views to make sure there's no overlap
    fun bind(skyscraper: Skyscraper, isDeletable: Boolean) {
        load(skyscraper).into(thumbnailView.skyscraper_img)
        thumbnailView.setTag(R.id.TAG_DETAIL, skyscraper)
       /* thumbnailView.detail_img.setOnClickListener {
            existingDetailNavigationListener.openDetailScreenFor(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }
        thumbnailView.text_detail_title.setOnClickListener {
            existingDetailNavigationListener.openDetailScreenFor(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }
        thumbnailView.btn_delete_detailRv.setOnClickListener {
            existingDetailNavigationListener.deleteDetail(thumbnailView.getTag(R.id.TAG_DETAIL) as Detail)
        }*/
        thumbnailView.text_skyscraper_description.text = skyscraper.description
        setDeletable(isDeletable)
    }

    private fun setDeletable(deletable: Boolean) {
        val deleteBtn = thumbnailView.btn_delete_skyscraper
        if (!deletable) {
            deleteBtn.visibility = View.INVISIBLE
            deleteBtn.isClickable = false
        } else {
            deleteBtn.visibility = View.VISIBLE
            deleteBtn.isClickable = true
        }
    }

    abstract fun load(skyscraper: Skyscraper): RequestBuilder<Drawable>

    class SkyscraperThumbnailBlueViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(skyscraper: Skyscraper): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_panel_blue_rv)
        }
    }
    class SkyscraperThumbnailDarkGreenViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(skyscraper: Skyscraper): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_panel_dark_green_rv)
        }
    }

    class SkyscraperThumbnailGreenViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(skyscraper: Skyscraper): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_panel_green_rv)
        }
    }

    class SkyscraperThumbnailPinkViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(skyscraper: Skyscraper): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_panel_blue)
        }
    }

    class SkyscraperThumbnailYellowViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(skyscraper: Skyscraper): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_panel_yellow_rv)
        }
    }



    interface SkyscraperHistoryNavigationListener {
        fun openSkyscraperHistoryScreenFor(skyscraper: Skyscraper)
        fun deleteSkyscraper(skyscraper: Skyscraper)
    }
}