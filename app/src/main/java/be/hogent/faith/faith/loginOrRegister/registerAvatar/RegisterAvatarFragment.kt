package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.RegisterUserViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarItemAdapter.OnAvatarClickListener
import be.hogent.faith.faith.loginOrRegister.registerUserInfo.RegisterUserInfoViewModel
import be.hogent.faith.util.TAG
import kotlinx.android.synthetic.main.fragment_register_avatar.avatar_rv_avatar
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A [Fragment] subclass which allows the user to register a new Avatar.
 *
 * Use the [RegisterAvatarFragment.newInstance] factory method to create an instance of this fragment.
 *
 */
const val SELECTION_ID = "avatarSelection"

class RegisterAvatarFragment : Fragment(), OnAvatarClickListener {

    private var navigation: AvatarFragmentNavigationListener? = null

    /**
     * ViewModel used for the avatars.
     */
    private val registerAvatarViewModel: RegisterAvatarViewModel by viewModel()

    private val userViewModel by inject<UserViewModel>(scope = getKoin().getScope(KoinModules.USER_SCOPE_ID))
    private val registerUserInfoViewModel by sharedViewModel<RegisterUserInfoViewModel>()
    private val registerUserViewModel by viewModel<RegisterUserViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentRegisterAvatarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register_avatar, container, false)
        binding.registerAvatarViewModel = registerAvatarViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        configureRecyclerViewAdapter()
        registerListeners()
    }

    private fun registerListeners() {
        registerUserViewModel.userRegisteredSuccessFully.observe(this, Observer { newUser ->
            userViewModel.setUser(newUser)
            navigation!!.goToCityScreen()
        })

        registerAvatarViewModel.finishRegistrationClicked.observe(this, Observer {
            Log.d(
                TAG, "Registering user with:" +
                        "username ${registerUserInfoViewModel.userName.value}, " +
                        "password ${registerUserInfoViewModel.password.value}, " +
                        "avatar ${registerAvatarViewModel.selectedAvatar}"
            )
            registerUserViewModel.registerUser(
                registerUserInfoViewModel.userName.value!!,
                registerUserInfoViewModel.password.value!!,
                registerAvatarViewModel.selectedAvatar!!
            )
            navigation!!.goToCityScreen()
        })

        registerAvatarViewModel.errorMessage.observe(this, Observer { errorMessageID ->
            Toast.makeText(context, errorMessageID, Toast.LENGTH_LONG).show()
        })
        registerUserViewModel.errorMessage.observe(this, Observer { errorMessageID ->
            Toast.makeText(context, errorMessageID, Toast.LENGTH_LONG).show()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AvatarFragmentNavigationListener) {
            navigation = context
        }
    }

    private fun configureRecyclerViewAdapter() {
        with(AvatarItemAdapter(this)) {
            avatars = registerAvatarViewModel.avatars.value!!
            avatar_rv_avatar.adapter = this
        }
        avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
    }

    interface AvatarFragmentNavigationListener {
        fun goToCityScreen()
    }

    override fun onAvatarClicked(index: Int) {
        registerAvatarViewModel.setSelectedAvatar(index)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        fun newInstance(): RegisterAvatarFragment {
            return RegisterAvatarFragment()
        }
    }
}
