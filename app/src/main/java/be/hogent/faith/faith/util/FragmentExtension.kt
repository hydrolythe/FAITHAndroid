package be.hogent.faith.faith.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * Returns a ViewModel of a certain type.
 * Source: https://android.jlelse.eu/android-arch-handling-clicks-and-single-actions-in-your-view-model-with-livedata-ab93d54bc9dc
 */
internal fun <T : ViewModel> Fragment.getViewModel(modelClass: Class<T>, viewModelFactory: ViewModelProvider.Factory? = null): T {
    return viewModelFactory?.let { ViewModelProviders.of(this, it).get(modelClass) } ?: ViewModelProviders.of(this).get(modelClass)
}

/**
 * replace a child fragment
 */
internal fun Fragment.replaceChildFragment(fragment: Fragment, frameId: Int) {
    val ft = getChildFragmentManager().beginTransaction()
    ft.replace(frameId, fragment)
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    ft.addToBackStack(null)
    ft.commit()
}