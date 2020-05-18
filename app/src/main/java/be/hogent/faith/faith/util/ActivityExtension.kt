package be.hogent.faith.faith.util

import android.content.Context
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        addToBackStack(null)
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, tag: String? = null) {
    supportFragmentManager.inTransaction {
        if (tag == null) replace(frameId, fragment) else replace(frameId, fragment, tag)
        addToBackStack(null)
    }
}

fun AppCompatActivity.getRotation(): Int {
    val display =
        (baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    return when (display.rotation) {
        Surface.ROTATION_90 -> R.integer.LANDSCAPE
        Surface.ROTATION_270 -> R.integer.LANDSCAPE
        Surface.ROTATION_180 -> R.integer.PORTRAIT
        Surface.ROTATION_0 -> R.integer.PORTRAIT
        else ->
            R.integer.PORTRAIT
    }
}
