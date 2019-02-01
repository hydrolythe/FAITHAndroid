package be.hogent.faith.faith.mainScreen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import org.koin.android.viewmodel.ext.android.viewModel

class MainScreenFragment : Fragment() {

    private val mainScreenViewModel: MainScreenViewModel by viewModel()
    private lateinit var mainScreenBinding: FragmentMainScreenBinding
    private lateinit var avatarView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_screen, container, false)
        mainScreenBinding.mainScreenViewModel = mainScreenViewModel
        mainScreenBinding.setLifecycleOwner(this)
        avatarView = mainScreenBinding.imageMainAvatar
        return mainScreenBinding.root
    }

    override fun onStart() {
        super.onStart()
        mainScreenViewModel.firstLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainFirstLocation)
        })
        mainScreenViewModel.secondLocation.observe(this, Observer {
            moveAvatarToLocationOf(mainScreenBinding.mainSecondLocation)
        })
    }

    /**
     * Moves the avatar to the top-left corner of the given view.
     */
    private fun moveAvatarToLocationOf(view: View) {
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
                    Log.i(TAG, "Animation finished, opening new Fragment")
                }
            })
        }
    }

    companion object {
        const val TAG = "MainScreenFragment"
    }
}
