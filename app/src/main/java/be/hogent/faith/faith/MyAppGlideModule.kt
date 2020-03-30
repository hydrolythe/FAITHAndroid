package be.hogent.faith.faith

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.io.InputStream

/**
 * load a firestore image using GlideApp, given a referencepath /users/uuid/projects/projectid/xxx.png
 */
fun loadImageIntoView(context: Context, referencePath: String, image: ImageView) {
    // TODO : encryptie
    if (referencePath.startsWith("users")) {
        GlideApp.with(context)
            .load(FirebaseStorage.getInstance().reference.child(referencePath)) // load the storagereference
            .apply(RequestOptions.centerCropTransform())
            .into(image)
    } else {
        Glide.with(context)
            .load(referencePath)
            .apply(RequestOptions.centerInsideTransform())
            .into(image)
    }
}

/**
 * Glide module to register [com.firebase.ui.storage.images.FirebaseImageLoader].
 * Generate Glide API and use GlideApp, not Glide itself. We need to make Glide use StorageReference to load images from Firebase
 * See: http://bumptech.github.io/glide/doc/generatedapi.html and https://medium.com/@egemenhamutcu/displaying-images-from-firebase-storage-using-glide-for-kotlin-projects-3e4950f6c103
 */
@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}
