package be.hogent.faith.faith.skyscraper.goal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperGoalBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin

class SkyscraperGoalFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperGoalBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var avatarOnDragListener: AvatarOnDragListener
    private lateinit var avatarOnTouchListener: AvatarOnTouchListener
    private lateinit var adapter: ActionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_skyscraper_goal, container, false)
        binding.lifecycleOwner = this
        avatarOnTouchListener =
            AvatarOnTouchListener()
        avatarOnDragListener =
            AvatarOnDragListener(avatarOnTouchListener)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
        setupRecyclerView()
        setOnclickListeners()
    }

    private fun updateUI() {
    }

    private fun setupRecyclerView() {
        val actionListener = object : ActionListener {
            override fun onActionClicked(action: Action) {

            }
        }
        adapter = ActionAdapter(actionListener)
        binding.rvGoalActions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGoalActions.adapter = adapter

        val list = arrayListOf<Action>()
        list.add(Action("Dit is een eerste actie"))
        list.add(Action("Dit is een tweede actie"))
        list.add(Action("Dit is een derde actie"))
        list.add(Action("Dit is een vierde actie"))
        adapter.updateActionsList(list)
    }

    private fun setOnclickListeners() {
        binding.btnSkyscraperReturn.setOnClickListener {
            navigation?.goBack()
        }
        binding.dragAvatar.setOnTouchListener(avatarOnTouchListener)
        binding.imageView16.setOnDragListener(avatarOnDragListener)

        //Betere manier vinden?
        binding.skyscraperAvatarDragDrop.avatarPosStairs1.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs2.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs3.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs4.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs5.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs6.setOnDragListener(avatarOnDragListener)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkyscraperNavigationListener) {
            navigation = context
        }
    }

    companion object {
        fun newInstance(): SkyscraperGoalFragment {
            return SkyscraperGoalFragment()
        }
    }

    interface SkyscraperNavigationListener {
        fun goBack()
    }
}
