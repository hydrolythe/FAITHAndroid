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

internal fun Fragment.showLoadingDialog(){

    // DialogFragment.show() will take care of adding the fragment
    // in a transaction.  We also want to remove any currently showing
    // dialog, so make our own transaction and take care of that here.

    val ft = fragmentManager!!.beginTransaction()
    val prev = fragmentManager!!.findFragmentByTag("loading_dialog")
    if (prev != null) {
        ft.remove(prev)
    }
    ft.addToBackStack(null)
    ft.commit()
    var loading = LoadingFragment.newInstance()
    loading.show(requireActivity().supportFragmentManager,"loading_dialog")
}

internal fun Fragment.dismissDialog(){
    val ft = fragmentManager!!.beginTransaction()
    val prev =  fragmentManager!!.findFragmentByTag("loading_dialog")
    if (prev != null) {
        ft.remove(prev)
    }
    ft.commit()
}