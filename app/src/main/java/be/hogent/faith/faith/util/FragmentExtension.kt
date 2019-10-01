package be.hogent.faith.faith.util

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_city_screen.guide_pole_left_end
import timber.log.Timber

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
    val ft = childFragmentManager.beginTransaction()
    ft.replace(frameId, fragment)
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    ft.commit()
}

/**
 * Adjusts the guideline (horizontally or vertically) based on the image which has been scaled.
 * @param guideline The guide line which needs to be scaled
 * @param padding The distance in pixels between the outer left (or top) side of the screen and the left side (top) of the background image
 * @param imageLength The length of the screen (width / height)
 */
internal fun Fragment.adjustGuidelineAfterScaling(
    guideline: Guideline,
    padding: Float,
    imageLength: Float,
    screenLength: Float
) {
    // Calculate percentage after scaling
    val originalPercentage =
        (guideline.layoutParams as ConstraintLayout.LayoutParams).guidePercent
    val newPercentage = ((originalPercentage * imageLength) + padding) / screenLength
    guide_pole_left_end.setGuidelinePercent(newPercentage)
    Timber.i("Changed %d from %f to %f", guideline.id, originalPercentage, newPercentage)

}