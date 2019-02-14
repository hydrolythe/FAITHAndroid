package be.hogent.faith.faith.util


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.core.view.ViewCompat.getRotation
import android.content.Context.WINDOW_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.view.WindowManager
import android.view.Display
import android.view.Surface
import be.hogent.faith.R


inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{replace(frameId, fragment)}
}

fun AppCompatActivity.getRotation(): Int{
    val display = (baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val rotation = display.rotation
    return when(rotation){
        Surface.ROTATION_90 ->  R.integer.LANDSCAPE
        Surface.ROTATION_270 ->  R.integer.LANDSCAPE
        Surface.ROTATION_180 ->  R.integer.PORTRAIT
        Surface.ROTATION_0 ->  R.integer.PORTRAIT
        else ->
             R.integer.PORTRAIT
    }
}