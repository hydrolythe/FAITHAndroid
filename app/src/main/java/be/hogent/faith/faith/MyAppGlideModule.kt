package be.hogent.faith.faith

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * load a firestore image using GlideApp, given a referencepath /users/uuid/projects/projectid/xxx.png
 */
fun loadImageIntoView(context: Context, referencePath: String, image: ImageView) {
    Glide.with(context)
        .load(referencePath)
        .apply(RequestOptions.centerInsideTransform())
        .into(image)
}
