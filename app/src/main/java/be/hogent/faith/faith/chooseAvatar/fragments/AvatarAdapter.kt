package be.hogent.faith.faith.chooseAvatar.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar


import com.bumptech.glide.RequestManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.avatar_rv_item.view.*

class AvatarAdapter(val viewModel: ListViewModel,
                    val lifecycleOwner: LifecycleOwner,
                    val reqManager : RequestManager) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    private var avatars : MutableList<Avatar> = mutableListOf()


    init {
        viewModel.avatars.observe(lifecycleOwner, Observer {
            avatars.clear()
            if(it != null){
                avatars.addAll(it)
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
        );
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
    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        val image = view.avatar_list_image;

        fun bind(avatar : Avatar){
            reqManager.load(avatar.imageUrl).into(image);

        }
    }

}