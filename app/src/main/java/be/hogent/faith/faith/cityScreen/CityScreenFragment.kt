package be.hogent.faith.faith.cityScreen

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCityScreenBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.util.adjustGuidelineAfterScaling
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_city_screen.background_city_screen
import kotlinx.android.synthetic.main.fragment_city_screen.guide_loc2_bottom
import kotlinx.android.synthetic.main.fragment_city_screen.guide_loc2_top
import kotlinx.android.synthetic.main.fragment_city_screen.guide_pole_bottom
import kotlinx.android.synthetic.main.fragment_city_screen.guide_pole_left_end
import kotlinx.android.synthetic.main.fragment_city_screen.guide_pole_left_start
import kotlinx.android.synthetic.main.fragment_city_screen.guide_pole_right_start
import kotlinx.android.synthetic.main.fragment_city_screen.image_main_avatar
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
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
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var mainScreenBinding: FragmentCityScreenBinding
    private lateinit var avatarView: View
    private val avatarProvider: AvatarProvider by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainScreenBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_city_screen, container, false)
        mainScreenBinding.cityScreenViewModel = cityScreenViewModel
        mainScreenBinding.lifecycleOwner = this
        avatarView = mainScreenBinding.imageMainAvatar

        return mainScreenBinding.root
    }

    override fun onStart() {
        super.onStart()

        registerListeners()
        val (screenWidth, screenHeight) = getScreenDimensions()

        // We wait for the image to be drawn and scaled, get its dimensions and then
        // set the guidlines properly
        background_city_screen.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                // Remove the listener otherwise infinite loop
                background_city_screen.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val (displayedWidth, displayedHeight) = getScaledDimensions(background_city_screen)

                // Calculate "padding"
                val left = (screenWidth - displayedWidth) / 2
                val top = (screenHeight - displayedHeight) / 2

                // Adjust the guide lines for the park
                adjustGuidelineAfterScaling(guide_pole_left_end, left, displayedWidth, screenWidth)
                adjustGuidelineAfterScaling(
                    guide_pole_left_start,
                    left,
                    displayedWidth,
                    screenWidth
                )
                adjustGuidelineAfterScaling(guide_loc2_bottom, top, displayedHeight, screenHeight)
                adjustGuidelineAfterScaling(guide_loc2_top, top, displayedHeight, screenHeight)

                // Adjust the guide lines for the library
                adjustGuidelineAfterScaling(
                    guide_pole_right_start,
                    left,
                    displayedWidth,
                    screenWidth
                )
                adjustGuidelineAfterScaling(
                    guide_pole_left_start,
                    left,
                    displayedWidth,
                    screenWidth
                )
                adjustGuidelineAfterScaling(guide_pole_bottom, top, displayedHeight, screenHeight)
            }
        })
    }

    /**
     * Get the width & height of the background image after scaling
     */
    private fun getScaledDimensions(view: ImageView): Pair<Float, Float> {
        val f = FloatArray(9)
        view.imageMatrix.getValues(f)
        val drawableHeight = background_city_screen.height.toFloat()
        val drawableWidth = background_city_screen.width.toFloat()
        val displayedWidth = drawableWidth * f[Matrix.MSCALE_X]
        val displayedHeight = drawableHeight * f[Matrix.MSCALE_Y]
        return Pair(displayedWidth, displayedHeight)
    }

    /**
     * Return a Pair with the screen width and the screen height.
     */
    private fun getScreenDimensions(): Pair<Float, Float> {
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        return Pair(size.x.toFloat(), size.y.toFloat())
    }

    private fun registerListeners() {
        mainScreenBinding.btnBackpack.setOnClickListener{
            navigation?.startBackpackFragment()
        }
        cityScreenViewModel.archiveClicked.observe(this, Observer {
            navigation?.startOverviewEventsFragment()
        })
        cityScreenViewModel.parkClicked.observe(this, Observer {
            navigation?.startEmotionCapture()
        })

        cityScreenViewModel.logoutSuccessFull.observe(this, Observer {
            navigation?.logOut()
        })

        userViewModel.user.observe(this, Observer { user ->
            Glide.with(context!!).load(avatarProvider.getAvatarDrawableStaan(user.avatarName))
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).into(image_main_avatar)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CityScreenNavigationListener) {
            navigation = context
        }
    }

    interface CityScreenNavigationListener {
        fun startEmotionCapture()
        fun startOverviewEventsFragment()
        fun logOut()
        fun startBackpackFragment()
    }
}
