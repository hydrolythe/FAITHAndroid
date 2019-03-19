package be.hogent.faith.faith.chooseAvatar.fragments

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.avatar_rv_item.view.*

/**
 * Adapter used by the Recyclerview which shows the Avatars in the [AvatarFragment].
 * The Recyclerview center their elements in the middle and snap the elements there.
 * The elements in the recyclerview are able to be selected.
 */
class AvatarItemAdapter() : RecyclerView.Adapter<AvatarItemAdapter.ViewHolder>() {

    /**
     * This [SelectionTracker] provides support for managing a selection of the items in the
     * RecyclerView instance.
     */
    var tracker: SelectionTracker<Long>? = null

    /**
     * The list of avatarItems which need to be displayed.
     */
    var avatarItems: List<Avatar> = emptyList()

    /**
     * Creates the ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.avatar_rv_item,
                parent,
                false
            )
        )
    }

    /**
     * Return the number of items (avatarItems) in the list of this adapter.
     */
    override fun getItemCount(): Int {
        return avatarItems.size
    }

    /**
     * Binds the image view of the list item to the desired image.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tracker?.let {
            holder.bind(avatarItems[position], it.isSelected(position.toLong()))
        }
    }

    /**
     * ViewHolder class, containing one image
     */
    inner class ViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        /**
         * Executes the binding of the data to the [ViewHolder]. Uses Glide] to load
         * the image.
         */
        fun bind(avatarItem: Avatar, isActivated: Boolean) {
            Glide.with(this.itemView.context).load(avatarItem.imageUrl).into(view.avatar_list_image)
            // This property is defined in res/drawable/item_background which in turn is used in the layout file
            // itself.
            itemView.isActivated = isActivated
        }

        /**
         * This function returns the details for the items in the recyclerview.
         * Required to use the [SelectionTracker].
         */
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            var d = Details()
            d.position = adapterPosition.toLong()
            return d
        }
    }

    /**
     * Extra information used by the selection library about the selected element in the Recyclerview.
     */
    internal class Details : ItemDetailsLookup.ItemDetails<Long>() {

        var position: Long = 0

        override fun getPosition(): Int {
            return position.toInt()
        }

        override fun getSelectionKey(): Long? {
            return position.toLong()
        }

        override fun inSelectionHotspot(e: MotionEvent): Boolean {
            return true
        }
    }

    /**
     * Class which return the keys for a certain element in the Recyclerview. In this case
     * we are still using the position in the RV. TODO: update to a Uri implementation.
     */
    internal class KeyProvider(adapter: RecyclerView.Adapter<*>) : ItemKeyProvider<Long>(ItemKeyProvider.SCOPE_MAPPED) {

        override fun getKey(position: Int): Long? {
            return position.toLong()
        }

        override fun getPosition(key: Long): Int {
            return key.toInt()
        }
    }

    /**
     * This class provides the selection library code necessary access
     * to information about items associated with android.view.MotionEvent
     */
    internal class DetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetailsLookup.ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val viewHolder = recyclerView.getChildViewHolder(view)
                if (viewHolder is ViewHolder) {
                    return viewHolder.getItemDetails()
                }
            }
            return null
        }
    }
}