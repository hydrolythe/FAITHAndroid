package be.hogent.faith.faith.skyscraper.goal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperGoalBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin
import java.util.Collections

class SkyscraperGoalFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperGoalBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var avatarOnDragListener: AvatarOnDragListener
    private lateinit var avatarOnTouchListener: AvatarOnTouchListener
    private lateinit var adapter: ActionAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    val list = arrayListOf<Action>()
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
        setupRecyclerView()
        setOnclickListeners()
    }

    private fun setupRecyclerView() {
        val actionListener = object : ActionListener {
            override fun onActionClicked(action: Action) {
                //TODO
            }
        }
        setupItemTouchHelper()
        adapter = ActionAdapter(actionListener)
        binding.rvGoalActions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGoalActions.adapter = adapter


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
        setDragDropListeners()

        binding.btnAddAction.setOnClickListener {
            list.add(Action("Dit is een actie"))
            adapter.updateActionsList(list)
        }
    }

    private fun setDragDropListeners() {
        binding.dragAvatar.setOnTouchListener(avatarOnTouchListener)

        //Betere manier vinden?
        binding.skyscraperAvatarDragDrop.avatarPosStairs1.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs2.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs3.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs4.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs5.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs6.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs7.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs8.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs9.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs10.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs11.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosStairs12.setOnDragListener(avatarOnDragListener)

        binding.skyscraperAvatarDragDrop.avatarPosLift1.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift2.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift3.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift4.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift5.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift6.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift7.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift8.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift9.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift10.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift11.setOnDragListener(avatarOnDragListener)
        binding.skyscraperAvatarDragDrop.avatarPosLift12.setOnDragListener(avatarOnDragListener)
    }
    private fun setupItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                Collections.swap(list, sourcePosition, targetPosition)
                adapter.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder as ActionAdapter.ViewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val deleteIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_trashcan)!!
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    c.save()
                    if (dX > 0) {
                        deleteIcon.setBounds(
                            itemView.left + iconMargin,
                            itemView.top + iconMargin,
                            itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMargin
                        )
                    } else {
                        deleteIcon.setBounds(
                            itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                            itemView.top + iconMargin,
                            itemView.right - iconMargin,
                            itemView.bottom - iconMargin
                        )
                    }

                    c.save()
                    if (dX > 0) {
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    } else {
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                    }
                    deleteIcon.draw(c)
                    c.restore()


                }
                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
            }

        })
        itemTouchHelper.attachToRecyclerView(binding.rvGoalActions)
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
