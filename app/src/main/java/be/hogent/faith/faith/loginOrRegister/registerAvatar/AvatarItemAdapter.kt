package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.avatar_rv_item.view.avatar_list_image

class AvatarItemAdapter(private val avatarClickListener: OnAvatarClickListener) :
    ListAdapter<Avatar, AvatarItemAdapter.ViewHolder>(AvatarItemDiffCallback()) {

    var selectedItem: Int = -1

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
     * Binds the image view of the list item to the desired image.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == selectedItem) {
            holder.bind(getItem(position), true)
        } else {
            holder.bind(getItem(position), false)
        }
    }

    /**
     * ViewHolder class, containing one image
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val avatarProvider =
            ResourceAvatarProvider(view.context)

        /**
         * Executes the binding of the data to the [ViewHolder].
         * Uses Glide to load the image.
         */
        fun bind(avatarItem: Avatar, isActivated: Boolean) {
            val avatarDrawable = avatarProvider.getAvatarDrawable(avatarItem.avatarName)
            view.isActivated = isActivated
            Glide.with(this.itemView.context).load(avatarDrawable).into(view.avatar_list_image)
            view.setTag(R.id.TAG_VIEWHOLDER, this)
            setClickListener()
        }

        private fun setClickListener() {
            view.setOnClickListener {
                val viewHolder = it.getTag(R.id.TAG_VIEWHOLDER) as RecyclerView.ViewHolder
                val position = viewHolder.adapterPosition
                viewHolder.itemView.isActivated = true
                val oldSelection = selectedItem
                selectedItem = position
                if (oldSelection != -1) {
                    notifyItemChanged(oldSelection)
                }
                notifyItemChanged(position)
                avatarClickListener.onAvatarClicked(position)
            }
        }
    }

    interface OnAvatarClickListener {
        fun onAvatarClicked(index: Int)
    }
}

class AvatarItemDiffCallback : DiffUtil.ItemCallback<Avatar>() {
    override fun areItemsTheSame(oldItem: Avatar, newItem: Avatar): Boolean {
        return oldItem.avatarName == newItem.avatarName
    }

    override fun areContentsTheSame(oldItem: Avatar, newItem: Avatar): Boolean {
        return oldItem == newItem
    }
}