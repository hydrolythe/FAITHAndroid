package be.hogent.faith.faith.util

import android.graphics.Color
import android.util.Log
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("errorMessageResId")
fun setErrorMessageResId(view: EditText, errorMessageResId: Int?) {
    if (errorMessageResId != null) {
        Log.d("BindingAdapter", "error ingesteld")
        val errorString = view.context.getString(errorMessageResId)
        view.setHint(errorString)
        view.setHintTextColor(Color.RED)
    }
}
