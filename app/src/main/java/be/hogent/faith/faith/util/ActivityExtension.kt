package be.hogent.faith.faith.util

import android.content.Context
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        addToBackStack(null)
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        addToBackStack(null)
    }
}

fun AppCompatActivity.getRotation(): Int {
    val display = (baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val rotation = display.rotation
    return when (rotation) {
        Surface.ROTATION_90 -> R.integer.LANDSCAPE
        Surface.ROTATION_270 -> R.integer.LANDSCAPE
        Surface.ROTATION_180 -> R.integer.PORTRAIT
        Surface.ROTATION_0 -> R.integer.PORTRAIT
        else ->
            R.integer.PORTRAIT
    }
}

internal fun <T : ViewModel> AppCompatActivity.getViewModel(modelClass: Class<T>, viewModelFactory: ViewModelProvider.Factory? = null): T {
    return viewModelFactory?.let { ViewModelProviders.of(this, it).get(modelClass) } ?: ViewModelProviders.of(this).get(modelClass)
}