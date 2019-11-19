package be.hogent.faith.faith.util

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorResId")
fun TextInputLayout.setErrorMessage(id: Int) {
    // If the id is 0 this means the error message has not yet been set and hence no error message
    // should be provided
    if (id != 0) {
        val errorMessage = resources.getString(id)
        error = errorMessage
    }
}