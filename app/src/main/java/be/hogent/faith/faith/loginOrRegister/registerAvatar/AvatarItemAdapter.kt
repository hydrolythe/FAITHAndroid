package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.avatar_rv_item.view.avatar_list_image



class AvatarItemAdapter() : RecyclerView.Adapter<AvatarItemAdapter.ViewHolder>() {


    /**
     * The list of avatars which need to be displayed.
     */
    var avatars: List<Avatar> = emptyList()

    var selectedItem : Int = -1

    /**
     * The Onclicklistener for when a view has been clicked.
     */
    lateinit var mOnItemClickListener: View.OnClickListener

    /**
     * Creates the ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                be.hogent.faith.R.layout.avatar_rv_item,
                parent,
                false
            )
        )
    }

    /**
     * Return the number of items (avatars) in the list of this adapter.
     */
    override fun getItemCount(): Int {
        return avatars.size
    }

    fun setOnItemClickListener(itemClickListener: View.OnClickListener) {
        mOnItemClickListener = itemClickListener
    }

    /**
     * Binds the image view of the list item to the desired image.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == selectedItem){
            holder.bind(avatars[position], true)
        }else {
            holder.bind(avatars[position], false)
        }
        holder.setClicklistener()
    }

    /**
     * ViewHolder class, containing one image
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // TODO: inject
        private val avatarProvider =
            ResourceAvatarProvider(view.context)

        /**
         * Executes the binding of the data to the [ViewHolder].
         * Uses Glide to load the image.
         */
        fun bind(avatarItem: Avatar, isActivated: Boolean) {
            val avatarDrawable = avatarProvider.getAvatarDrawable(avatarItem.avatarName)
            view.isActivated= isActivated
            Glide.with(this.itemView.context).load(avatarDrawable).into(view.avatar_list_image)
        }

        fun setClicklistener(){
            view.setTag(this)
            view.setOnClickListener(mOnItemClickListener)
        }

    }


}