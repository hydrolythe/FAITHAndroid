package be.hogent.faith.faith.loginOrRegister

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_welcome.background_welcome
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named

class WelcomeFragment : Fragment() {

    private var navigation: WelcomeNavigationListener? = null

    private val welcomeViewModel by viewModel<WelcomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: be.hogent.faith.databinding.FragmentWelcomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        binding.welcomeViewModel = welcomeViewModel

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        registerListeners()
        setBackgroundImage()
    }

    private fun setBackgroundImage() {
        Glide.with(requireContext())
            .load(R.drawable.loginscherm)
            .into(background_welcome)
    }

    private fun registerListeners() {
        welcomeViewModel.registerButtonClicked.observe(this, Observer {
            navigation!!.goToRegistrationScreen()
        })
        welcomeViewModel.UserLoggedInSuccessFully.observe(this, Observer {
            onLoginSuccess()
        })
        welcomeViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WelcomeNavigationListener) {
            navigation = context
        }
    }

    interface WelcomeNavigationListener {
        fun goToRegistrationScreen()
        fun goToCityScreen()
    }

    private fun onLoginSuccess() {
        val scope = try {
            getKoin().createScope(KoinModules.USER_SCOPE_ID, named(USER_SCOPE_NAME))
        } catch (e: ScopeAlreadyCreatedException) {
            getKoin().getScope(KoinModules.USER_SCOPE_ID)
        }
        val userViewModel: UserViewModel = scope.get()
        //TODO : get the user from Firebase
        userViewModel.setUser(User("testuser", avatarName = "meisje_stoer"))
        navigation!!.goToCityScreen()
    }


    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }
}
