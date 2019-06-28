package be.hogent.faith.faith.loginOrRegister.registerUserInfo

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
import be.hogent.faith.util.TAG
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_register_userinfo.background_register_userinfo
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
        setBackgroundImage()
    }

    private fun setBackgroundImage() {
        Glide.with(requireContext())
            .load(R.drawable.register_background_userinfo)
            .into(background_register_userinfo)
    }

    private fun registerListeners() {
        registerUserInfoViewModel.confirmUserInfoClicked.observe(this, Observer {
            navigation!!.goToRegisterAvatarScreen()
            Log.i(TAG,"Jooo")
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
