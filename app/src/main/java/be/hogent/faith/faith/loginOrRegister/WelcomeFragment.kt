package be.hogent.faith.faith.loginOrRegister

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import org.koin.android.viewmodel.ext.android.viewModel

class WelcomeFragment : Fragment() {

    private var navigation: WelcomeNavigationListener? = null

    private val welcomeViewModel by viewModel<WelcomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentWelcomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        binding.welcomeViewModel = welcomeViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        registerListeners()
    }

    private fun registerListeners() {
        welcomeViewModel.registerButtonClicked.observe(this, Observer<Any> {
            navigation!!.goToRegistrationScreen()
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
