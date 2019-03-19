package be.hogent.faith.faith.mainScreen

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
import be.hogent.faith.databinding.FragmentMainScreenBinding
import be.hogent.faith.faith.util.TAG
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Fragment displaying the starting screen of the application (the city landscape).
 * This screen allows the user to click on several pre-defined areas.
 * Upon clicking an area the corresponding screen will open.
 */
class MainScreenFragment : Fragment() {

    private var navigation: MainScreenNavigationListener? = null
    private val mainScreenViewModel: MainScreenViewModel by viewModel()
    private lateinit var mainScreenBinding: FragmentMainScreenBinding
    private lateinit var avatarView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_screen, container, false)
        mainScreenBinding.mainScreenViewModel = mainScreenViewModel
        mainScreenBinding.lifecycleOwner = this
        avatarView = mainScreenBinding.imageMainAvatar
        return mainScreenBinding.root
    }

    override fun onStart() {
        super.onStart()
        mainScreenViewModel.firstLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainFirstLocation) {}
        })
        mainScreenViewModel.secondLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainSecondLocation) { navigation?.startEventDetailsFragment() }
        })
        mainScreenViewModel.thirdLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainThirdLocation) { navigation?.startOverviewEventsFragment() }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainScreenNavigationListener) {
            navigation = context
        }
    }

    /**
     * Moves the avatar View to the top-left corner of the given View.
     * Once the animation is finished it calls the [onAnimationEndCall].
     */
    private fun moveAvatarToLocationOf(view: View, onAnimationEndCall: () -> Unit) {
        // No need to animate if the avatar is already there
        if (avatarView.x == view.x && avatarView.y == view.y) {
            Log.i(TAG, "Not moving the avatar as we're already at the view's location")
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

    interface MainScreenNavigationListener {
        fun startEventDetailsFragment()
        fun startOverviewEventsFragment()
    }
}
