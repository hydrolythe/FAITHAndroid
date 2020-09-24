package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarItemAdapter.OnAvatarClickListener
import be.hogent.faith.faith.util.state.Resource
import be.hogent.faith.faith.util.state.ResourceState
import kotlinx.android.synthetic.main.fragment_login.progress
import kotlinx.android.synthetic.main.fragment_register_avatar.avatar_rv_avatar
import kotlinx.android.synthetic.main.fragment_register_avatar.btn_register_skincolor_blank
import kotlinx.android.synthetic.main.fragment_register_avatar.btn_register_skincolor_darkbrown
import kotlinx.android.synthetic.main.fragment_register_avatar.btn_register_skincolor_lightbrown
import org.koin.android.viewmodel.ext.android.sharedViewModel

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
    private val registerAvatarViewModel: RegisterAvatarViewModel by sharedViewModel()
    private lateinit var adapter: AvatarItemAdapter

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
            navigation!!.initialiseUser()
        })

        registerAvatarViewModel.errorMessage.observe(this, Observer { errorMessageID ->
            Toast.makeText(context, errorMessageID, Toast.LENGTH_LONG).show()
        })
        registerAvatarViewModel.userRegisteredState.observe(this, Observer {
            it?.let {
                handleDataState(it)
            }
        })
        registerAvatarViewModel.selectedSkinColor.observe(this, Observer {
            setSkinColorDrawable(it, SkinColor.blank, btn_register_skincolor_blank, R.color.skin_blank)
            setSkinColorDrawable(
                it,
                SkinColor.light_brown,
                btn_register_skincolor_lightbrown,
                R.color.skin_lightbrown
            )
            setSkinColorDrawable(
                it,
                SkinColor.dark_brown,
                btn_register_skincolor_darkbrown,
                R.color.skin_darkbrown
            )
        })
        registerAvatarViewModel.avatars.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
                if (adapter.selectedItem != -1) avatar_rv_avatar.post {
                    avatar_rv_avatar.scrollToPosition(
                        adapter.selectedItem
                    )
                }
            }
        })
    }

    private fun setSkinColorDrawable(
        selectedColor: SkinColor,
        color: SkinColor,
        view: ImageView,
        fillColor: Int
    ) {
        val gradientDrawable =
            getDrawable(this.requireContext(), R.drawable.skin_color) as GradientDrawable
        gradientDrawable.setColor(ContextCompat.getColor(this.requireContext(), fillColor))
        gradientDrawable.setStroke(
            2,
            if (selectedColor != color) ContextCompat.getColor(
                this.requireContext(), fillColor
            ) else Color.BLACK
        )
        view.background = gradientDrawable
    }

    private fun handleDataState(resource: Resource<Unit>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                navigation!!.userIsRegistered() // progress bar must stay visible
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
        if (context is AvatarFragmentNavigationListener) {
            navigation = context
        }
    }

    private fun configureRecyclerViewAdapter() {
        adapter = AvatarItemAdapter(this)
        avatar_rv_avatar.adapter = adapter
        avatar_rv_avatar.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
    }

    interface AvatarFragmentNavigationListener {
        fun userIsRegistered()
        fun initialiseUser()
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
