package be.hogent.faith.faith.loginOrRegister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.avatar_rv_item.view.avatar_list_image

/**
 * Adapter used by the Recyclerview which shows the Avatars in the [RegisterAvatarFragment].
 * The Recyclerview center their elements in the middle and snap the elements there.
 * The elements in the recyclerview are able to be selected.
 */
class AvatarItemAdapter : RecyclerView.Adapter<AvatarItemAdapter.ViewHolder>() {


    /**
     * The list of avatars which need to be displayed.
     */
    var avatars: List<Avatar> = emptyList()

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

    fun loadAvatar(newAvatar: List<Avatar>) {
        avatars = newAvatar
    }

    /**
     * Return the number of items (avatars) in the list of this adapter.
     */
    override fun getItemCount(): Int {
        return avatars.size
    }

    /**
     * Binds the image view of the list item to the desired image.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(avatars[position])
    }

    /**
     * ViewHolder class, containing one image
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // TODO: inject
        private val avatarProvider = ResourceAvatarProvider(view.context)

        /**
         * Executes the binding of the data to the [ViewHolder].
         * Uses Glide to load the image.
         */
        fun bind(avatarItem: Avatar) {
            val avatarDrawable = avatarProvider.getAvatarDrawable(avatarItem.avatarName)
            Glide.with(this.itemView.context).load(avatarDrawable).into(view.avatar_list_image)
        }

    }


}