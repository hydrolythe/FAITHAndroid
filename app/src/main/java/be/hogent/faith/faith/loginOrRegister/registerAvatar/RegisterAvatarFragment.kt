package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.content.Context
import android.os.Bundle
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
import be.hogent.faith.faith.loginOrRegister.RegisterUserViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarItemAdapter.OnAvatarClickListener
import kotlinx.android.synthetic.main.fragment_register_avatar.avatar_rv_avatar
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

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
    private val registerUserViewModel by sharedViewModel<RegisterUserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        registerAvatarViewModel.finishRegistrationClicked.observe(this, Observer {
            registerUserViewModel.setAvatar(registerAvatarViewModel.selectedAvatar!!)
            registerUserViewModel.register()
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
        avatar_rv_avatar.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
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
