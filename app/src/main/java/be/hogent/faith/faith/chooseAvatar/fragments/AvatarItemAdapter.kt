package be.hogent.faith.faith.chooseAvatar.fragments

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.AvatarItem


import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.avatar_rv_item.view.*

/**
 * Adapter used by the Recyclerview which shows the Avatars in the [AvatarFragment].
 */
class AvatarItemAdapter(
    viewModel: AvatarItemViewModel,
    lifecycleOwner: LifecycleOwner,
    val reqManager: RequestManager
) : RecyclerView.Adapter<AvatarItemAdapter.ViewHolder>() {

    /**
     * The list of avatarItems which need to be displayed.
     */
    private var avatarItems: MutableList<AvatarItem> = mutableListOf()


    init {
        viewModel.avatarItems.observe(lifecycleOwner, Observer {

            if(it != null){
                avatarItems.clear()
                avatarItems.addAll(it)
            }
            notifyDataSetChanged()
        })
    }


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
        holder.bind(avatarItems[position])

    }

    /**
     * ViewHolder class, containing one image
     */
    inner class ViewHolder(view: View, private val image: ImageView = view.avatar_list_image) :
        RecyclerView.ViewHolder(view) {

        /**
         * Executes the binding of the data to the [ViewHolder]. Uses Glide] to load
         * the image.
         */
        fun bind(avatarItem: AvatarItem) {
            reqManager.load(avatarItem.imageUrl).into(image)
        }
    }

}