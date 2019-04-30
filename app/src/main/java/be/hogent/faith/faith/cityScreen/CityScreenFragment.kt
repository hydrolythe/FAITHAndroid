package be.hogent.faith.faith.cityScreen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCityScreenBinding
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.util.TAG
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Fragment displaying the starting screen of the application (the city landscape).
 * This screen allows the user to click on several pre-defined areas.
 * Upon clicking an area the corresponding screen will open.
 */
class CityScreenFragment : Fragment() {

    companion object {
        fun newInstance() = CityScreenFragment()
    }

    private var navigation: CityScreenNavigationListener? = null
    private val cityScreenViewModel: CityScreenViewModel by viewModel()
    private lateinit var mainScreenBinding: FragmentCityScreenBinding
    private lateinit var avatarView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_city_screen, container, false)
        mainScreenBinding.cityScreenViewModel = cityScreenViewModel
        mainScreenBinding.lifecycleOwner = this
        avatarView = mainScreenBinding.imageMainAvatar
        return mainScreenBinding.root
    }

    override fun onStart() {
        super.onStart()
        registerListeners()
    }

    private fun registerListeners() {
        cityScreenViewModel.firstLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainFirstLocation) {}
        })
        cityScreenViewModel.secondLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainSecondLocation) { navigation?.startEmotionCapture() }
        })
        cityScreenViewModel.thirdLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainThirdLocation) { navigation?.startOverviewEventsFragment() }
        })

        cityScreenViewModel.logOutClicked.observe(this, Observer {
            // Close the user scope so a new one is created when logging in with another user
            getKoin().getScope(KoinModules.USER_SCOPE_ID).close()
            // TODO: go back to login Screen
        })
    }

    override fun onResume() {
        super.onResume()
        moveAvatarToLocationOf(mainScreenBinding.mainStartLocation) {}
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CityScreenNavigationListener) {
            navigation = context
        }
    }

    /**
     * Moves the avatarName View to the top-left corner of the given View.
     * Once the animation is finished it calls the [onAnimationEndCall].
     */
    private fun moveAvatarToLocationOf(view: View, onAnimationEndCall: () -> Unit) {
        // No need to animate if the avatarName is already there
        if (avatarView.x == view.x && avatarView.y == view.y) {
            Log.i(TAG, "Not moving the avatarName as we're already at the view's location")
            return
        }
        val xTranslation = ObjectAnimator.ofFloat(avatarView, "x", view.x).apply {
            duration = 1000
        }
        val yTranslation = ObjectAnimator.ofFloat(avatarView, "y", view.y).apply {
            duration = 1000
        }
        // Bundle both animations so we can start them synchronously
        AnimatorSet().apply {
            play(xTranslation).with(yTranslation)
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    onAnimationEndCall()
                }
            })
        }
    }

    interface CityScreenNavigationListener {
        fun startEmotionCapture()
        fun startOverviewEventsFragment()
    }
}
