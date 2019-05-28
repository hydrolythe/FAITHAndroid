package be.hogent.faith.faith.loginOrRegister

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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.util.TAG
import kotlinx.android.synthetic.main.fragment_avatar.avatar_rv_avatar
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A [Fragment] subclass which allows the user to register a new Avatar.
 *
 * Use the [RegisterAvatarFragment.newInstance] factory method to create an instance of this fragment.
 *
 */
const val SELECTION_ID = "avatarSelection"

class RegisterAvatarFragment : Fragment() {

    private var navigation: AvatarFragmentNavigationListener? = null

    /**
     * ViewModel used for the avatars.
     */
    private val avatarViewModel: AvatarViewModel by viewModel()

    private val userViewModel by inject<UserViewModel>(scope = getKoin().getScope(KoinModules.USER_SCOPE_ID))


    override fun onStart() {
        super.onStart()
        configureRecyclerViewAdapter()
        registerListeners()


    }

    private fun registerListeners() {
        avatarViewModel.nextButtonClicked.observe(this, Observer<Any> {
            navigation!!.goToCityScreen()
        })


        avatarViewModel.userSaveFailed.observe(this, Observer { errorMessage ->
            Log.e(TAG, errorMessage)
            Toast.makeText(context, R.string.error_save_user_failed, Toast.LENGTH_LONG).show()
        })

        avatarViewModel.userSavedSuccessFully.observe(this, Observer { newUser ->
            userViewModel.setUser(newUser)
        })

        avatarViewModel.inputErrorMessageID.observe(this, Observer { errorMessageID ->
            Toast.makeText(context, errorMessageID, Toast.LENGTH_LONG).show()
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AvatarFragmentNavigationListener) {
            navigation = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentAvatarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_avatar, container, false)
        binding.avatarViewModel = avatarViewModel
        return binding.root
    }



    private fun configureRecyclerViewAdapter() {
        val avatarAdapter = AvatarItemAdapter()
        avatar_rv_avatar.adapter = avatarAdapter
        LinearSnapHelper().attachToRecyclerView(avatar_rv_avatar)
        avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        avatarViewModel.avatars.observe(this, Observer {
            avatarAdapter.loadAvatar(it ?: emptyList())
            avatarAdapter.notifyDataSetChanged()
        })
    }

    interface AvatarFragmentNavigationListener {
        fun goToCityScreen()
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
