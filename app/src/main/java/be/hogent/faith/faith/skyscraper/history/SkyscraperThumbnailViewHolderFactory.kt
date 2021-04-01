package be.hogent.faith.faith.skyscraper.history

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.models.goals.Goal
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperHistoryNavigationListener
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailBlueViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailDarkGreenViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailGreenViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailPinkViewHolder
import be.hogent.faith.faith.skyscraper.history.SkyscraperThumbnailViewHolder.SkyscraperThumbnailYellowViewHolder
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import kotlinx.android.synthetic.main.fragment_skyscraper_history.view.btn_delete_skyscraper
import kotlinx.android.synthetic.main.skyscraper_thumbnail_item_rv.view.skyscraper_img
import kotlinx.android.synthetic.main.skyscraper_thumbnail_item_rv.view.text_skyscraper_description
import org.koin.core.KoinComponent

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
            SkyscraperColors.SKYSCRAPER_RED.value -> createSkyscraperThumbnailPinkViewHolder(
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
    private val skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
) : RecyclerView.ViewHolder(thumbnailView), KoinComponent {

    fun bind(goal: Goal, isDeletable: Boolean) {
        load().into(thumbnailView.skyscraper_img)
        thumbnailView.setTag(R.id.TAG_GOAL, goal)
        thumbnailView.text_skyscraper_description.text = goal.description
        thumbnailView.setOnClickListener {
            skyscraperHistoryNavigationListener.openSkyscraperHistoryScreenFor(goal)
        }
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

    abstract fun load(): RequestBuilder<Drawable>

    class SkyscraperThumbnailBlueViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_blue_panel)
        }
    }
    class SkyscraperThumbnailDarkGreenViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_darkgreen_panel)
        }
    }

    class SkyscraperThumbnailGreenViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_green_panel)
        }
    }

    class SkyscraperThumbnailPinkViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_panel_blue)
        }
    }

    class SkyscraperThumbnailYellowViewHolder(
        imageView: LinearLayout,
        skyscraperHistoryNavigationListener: SkyscraperHistoryNavigationListener
    ) : SkyscraperThumbnailViewHolder(imageView, skyscraperHistoryNavigationListener) {

        override fun load(): RequestBuilder<Drawable> {
            return Glide.with(thumbnailView).load(R.drawable.skyscraper_yellow_panel)
        }
    }

    interface SkyscraperHistoryNavigationListener {
        fun openSkyscraperHistoryScreenFor(goal: Goal)
        fun deleteSkyscraper(goal: Goal)
    }
}