package be.hogent.faith.faith.loginOrRegister

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import kotlinx.android.synthetic.main.fragment_login.progress
import org.koin.android.viewmodel.ext.android.sharedViewModel

class WelcomeFragment : Fragment() {

    private var navigation: WelcomeNavigationListener? = null

    private val welcomeViewModel by sharedViewModel<WelcomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: be.hogent.faith.databinding.FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.welcomeViewModel = welcomeViewModel

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        registerListeners()
    }

    private fun registerListeners() {
        // user wants to register
        welcomeViewModel.registerButtonClicked.observe(this, Observer {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://faith-dev-38aa1.ey.r.appspot.com"))
            startActivity(intent)
        })

        // user is logging in....
        welcomeViewModel.userLoggedInState.observe(this, Observer {
            it?.let {
                handleDataStateLogIn(it)
            }
        })
    }

    // handle state when user is logging in
    private fun handleDataStateLogIn(resource: Resource<Unit>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                navigation!!.userIsLoggedIn()
            }
            ResourceState.LOADING -> {
                progress.visibility = View.VISIBLE
            }
            ResourceState.ERROR -> {
                progress.visibility = View.GONE
                Toast.makeText(context, resource.message!!, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WelcomeNavigationListener) {
            navigation = context
        }
    }

    interface WelcomeNavigationListener {
        fun userIsLoggedIn()
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
