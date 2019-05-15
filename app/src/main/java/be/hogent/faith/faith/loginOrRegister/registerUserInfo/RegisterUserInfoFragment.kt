package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RegisterUserInfoFragment : Fragment() {

    private var navigation: RegisterUserInfoNavigationListener? = null

    /**
     * ViewModel used for the avatars.
     */
    private val registerUserInfoViewModel: RegisterUserInfoViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentRegisterUserinfoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register_userinfo, container, false)
        binding.registerUserInfoViewModel = registerUserInfoViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        registerListeners()
    }

    private fun registerListeners() {
        registerUserInfoViewModel.confirmUserInfoClicked.observe(this, Observer {
            navigation!!.goToRegisterAvatarScreen()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RegisterUserInfoNavigationListener) {
            navigation = context
        }
    }

    interface RegisterUserInfoNavigationListener {
        fun goToRegisterAvatarScreen()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        fun newInstance(): RegisterUserInfoFragment {
            return RegisterUserInfoFragment()
        }
    }
}
