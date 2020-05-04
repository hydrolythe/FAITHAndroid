package be.hogent.faith.faith.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

/**
 * replace a child fragment
 */
internal fun Fragment.replaceChildFragment(fragment: Fragment, frameId: Int) {
    val ft = childFragmentManager.beginTransaction()
    ft.replace(frameId, fragment)
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    ft.commit()
}
