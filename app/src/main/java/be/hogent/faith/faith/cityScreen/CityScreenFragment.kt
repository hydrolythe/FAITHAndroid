package be.hogent.faith.faith.cityScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCityScreenBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
        // avatarView = mainScreenBinding.imageMainAvatar

        return mainScreenBinding.root
    }

    override fun onStart() {
        super.onStart()
        registerListeners()
    }

    private fun registerListeners() {
        cityScreenViewModel.libraryClicked.observe(this, Observer {
            navigation?.startLibrary()
        })
        cityScreenViewModel.parkClicked.observe(this, Observer {
            navigation?.startEmotionCapture()
        })
        cityScreenViewModel.logoutSuccessFull.observe(this, Observer {
            navigation?.logOut()
        })
        cityScreenViewModel.backpackClicked.observe(this, Observer {
            navigation?.startBackpack()
        })
        cityScreenViewModel.treehouseClicked.observe(this, Observer {
            navigation?.startTreasureChest()
        })
        cityScreenViewModel.cinemaClicked.observe(this, Observer {
            navigation?.startCinema()
        })
        cityScreenViewModel.skyscraperClicked.observe(this, Observer {
            navigation?.startSkyscraperFragment()
        })

        userViewModel.user.observe(this, Observer { user ->
            Glide.with(requireContext()).load(avatarProvider.getAvatarDrawableStaan(user.avatarName))
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
        fun startLibrary()
        fun logOut()
        fun startBackpack()
        fun startCinema()
        fun startTreasureChest()
        fun startSkyscraperFragment()
    }
}
