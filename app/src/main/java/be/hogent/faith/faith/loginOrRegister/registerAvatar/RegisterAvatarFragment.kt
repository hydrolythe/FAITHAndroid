package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.RegisterUserViewModel
import be.hogent.faith.faith.loginOrRegister.registerUserInfo.RegisterUserInfoViewModel
import be.hogent.faith.faith.util.getRotation
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

class RegisterAvatarFragment : Fragment() {

    private var navigation: AvatarFragmentNavigationListener? = null

    /**
     * ViewModel used for the avatars.
     */
    private val registerAvatarViewModel: RegisterAvatarViewModel by viewModel()

    private val userViewModel by inject<UserViewModel>(scope = getKoin().getScope(KoinModules.USER_SCOPE_ID))
    private val registerUserInfoViewModel by sharedViewModel<RegisterUserInfoViewModel>()

    private val registerUserViewModel by viewModel<RegisterUserViewModel>()

    /**
     * Object used to track the selection on the Recyclerview
     */
    private var avatarTracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            // If the Fragment is still in memory restore the state
            avatarTracker?.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentRegisterAvatarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register_avatar, container, false)
        binding.registerAvatarViewModel = registerAvatarViewModel
        binding.registerUserInfoViewModel = registerUserInfoViewModel
        binding.registerUserViewModel = registerUserViewModel
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

        registerUserViewModel.finishRegistrationClicked.observe(this, Observer {
            Log.d(
                TAG, "Registering user with:" +
                        "username ${registerUserInfoViewModel.userName.value}, " +
                        "password ${registerUserInfoViewModel.password.value}, " +
                        "avatar ${registerAvatarViewModel.selectedAvatar.value}"
            )
            registerUserViewModel.registerUser(
                registerUserInfoViewModel.userName.value!!,
                registerUserInfoViewModel.password.value!!,
                //TODO: fix so we can used [RegisterAvatarViewModel.selectedAvatar]
                registerAvatarViewModel.avatars.value!![registerAvatarViewModel.selectedItem.value!!.toInt()]
            )
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

    private fun setRecyclerViewOrientation() {
        // Check here so we can use FragmentScenario to test
        val orientation = if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).getRotation()
        } else {
            R.integer.LANDSCAPE
        }
        when (orientation) {
            R.integer.PORTRAIT -> {
                avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                avatar_rv_avatar.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            R.integer.LANDSCAPE -> {
                avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                avatar_rv_avatar.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            }
        }
    }

    /*
     * Checks for the orientation of the device and sets the LayoutManager for the Adapter based on this.
     */
    private fun configureRecyclerViewAdapter() {
        val avatarAdapter = AvatarItemAdapter()
        avatar_rv_avatar.adapter = avatarAdapter

        LinearSnapHelper().attachToRecyclerView(avatar_rv_avatar)

        setRecyclerViewOrientation()

        avatarTracker = SelectionTracker.Builder<Long>(
            SELECTION_ID,
            avatar_rv_avatar,
            AvatarItemAdapter.KeyProvider(),
            AvatarItemAdapter.DetailsLookup(avatar_rv_avatar),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectSingleAnything()
        ).build()

        (avatar_rv_avatar.adapter as AvatarItemAdapter).selectionTracker = avatarTracker

        if (registerAvatarViewModel.avatarWasSelected()) {
            avatarTracker?.select(registerAvatarViewModel.selectedItem.value!!)
            avatar_rv_avatar.smoothScrollToPosition(registerAvatarViewModel.selectedItem.value!!.toInt())
        }

        // We also need to observe selection changes in the RecyclerView.
        avatarTracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                val iterator = avatarTracker?.selection?.iterator()
                if (iterator!!.hasNext()) {
                    val itemPressed = iterator.next()
                    registerAvatarViewModel.setSelectedItem(itemPressed)
                }
            }
        })

        // Observe the changes in the list of the avatars and update the adapter
        registerAvatarViewModel.avatars.observe(this, Observer<List<Avatar>> { avatarList ->
            avatarList?.let {
                avatarAdapter.avatars = avatarList
                avatarAdapter.notifyDataSetChanged()
            }
        })
    }

    /**
     * We need to save the instance state of the selectionTracker, otherwise
     * the selected item will be lost.
     * TODO: See if this is possible without the onSaveinstanceState but with an observer.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        avatarTracker!!.onSaveInstanceState(outState)
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