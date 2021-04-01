package be.hogent.faith.faith.skyscraper.goal

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperGoalBinding
import be.hogent.faith.faith.models.goals.ReachGoalWay
import be.hogent.faith.faith.models.goals.SUBGOALS_UPPER_BOUND
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.models.goals.Goal
import be.hogent.faith.faith.models.goals.GoalColor
import be.hogent.faith.faith.models.goals.SubGoal
import kotlinx.android.synthetic.main.skyscraper_subgoal_rv_item.txt_subgoal_description
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.max

private const val GOAL = "The goal to be shown"
private const val THUMB_WIDTH = 24
private const val THUMB_HEIGHT = 55

class SkyscraperGoalFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperGoalBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val user = userViewModel.user.value!!
    private lateinit var goalViewModel: GoalViewModel
    private lateinit var actionAdapter: ActionAdapter
    private lateinit var subgoalAdapter: SubGoalAdapter
    private val avatarProvider: AvatarProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadExistingGoal()
    }

    private fun loadExistingGoal() {
        val goal = arguments?.getSerializable(GOAL) as Goal
        goalViewModel =
            getViewModel { parametersOf(user.getGoal(goal.uuid)!!.copy(), user) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_skyscraper_goal, container, false)
        binding.lifecycleOwner = this
        binding.goalViewModel = goalViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setUpSkyscraper()
        setupRecyclerView()
        setThumb(userViewModel.user.value!!.avatarName)
        startListeners()
        startViewModelListeners()
    }

    private fun setUpSkyscraper() {
        val goal = goalViewModel.goal.value!!

        when (goal.goalColor) {
            GoalColor.BLUE -> {
                binding.skyscraper.setImageResource(R.drawable.skyscraper_blue_empty)
                binding.skyscraperPanel.setImageResource(R.drawable.skyscraper_blue_panelbig)
                binding.skyscraperElevator.setImageResource(R.drawable.elevator_blue)
            }
            GoalColor.YELLOW -> {
                binding.skyscraper.setImageResource(R.drawable.skyscraper_yellow_empty)
                binding.skyscraperPanel.setImageResource(R.drawable.skyscraper_yellow_panelbig)
                binding.skyscraperElevator.setImageResource(R.drawable.elevator_yellow)
            }
            GoalColor.RED -> {
                binding.skyscraper.setImageResource(R.drawable.skyscraper_red_empty)
                binding.skyscraperPanel.setImageResource(R.drawable.skyscraper_red_panelbig)
                binding.skyscraperElevator.setImageResource(R.drawable.elevator_red)
            }
            GoalColor.DARKGREEN -> {
                binding.skyscraper.setImageResource(R.drawable.skyscraper_darkgreen_empty)
                binding.skyscraperPanel.setImageResource(R.drawable.skyscraper_darkgreen_panelbig)
                binding.skyscraperElevator.setImageResource(R.drawable.elevator_darkgreen)
            }
            GoalColor.GREEN -> {
                binding.skyscraper.setImageResource(R.drawable.skyscraper_green_empty)
                binding.skyscraperPanel.setImageResource(R.drawable.skyscraper_green_panelbig)
                binding.skyscraperElevator.setImageResource(R.drawable.elevator_green)
            }
            else -> R.drawable.skyscraper_blue_base
        }

        binding.skyscraperGoalDescription.setTextColor(
            if (goal.goalColor == GoalColor.YELLOW)
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            else ContextCompat.getColor(requireContext(), R.color.color_white)
        )
    }

    private fun startViewModelListeners() {
        goalViewModel.onAvatarPlaceChanged.observe(this, Observer {
            val goal = goalViewModel.goal.value!!
            binding.skyscraperRoofAvatar.visibility = if (goal.isCompleted) View.VISIBLE else View.GONE

            if (!goal.isCompleted && goal.chosenReachGoalWay == ReachGoalWay.Stairs) {
                binding.dragAvatar.visibility = View.VISIBLE
                calculatePositionAvatar()
            } else binding.dragAvatar.visibility = View.GONE

            binding.skyscraperElevatorSeekbar.progress = goal.currentPositionAvatar
            binding.skyscraperElevatorSeekbar.visibility =
                if (!goal.isCompleted && goal.chosenReachGoalWay == ReachGoalWay.Elevator) View.VISIBLE else View.GONE

            binding.skyscraperRopeSeekbar.progress = goal.currentPositionAvatar
            binding.skyscraperRopeSeekbar.visibility =
                if (!goal.isCompleted && goal.chosenReachGoalWay == ReachGoalWay.Rope) View.VISIBLE else View.GONE
        })

        goalViewModel.selectedSubGoal.observe(this, Observer { subgoal ->
            if (subgoal?.second?.description.isNullOrEmpty()) {
                txt_subgoal_description.requestFocus()
            }
        })

        // Update adapter when event changes
        goalViewModel.actions.observe(this, Observer { actions ->
            actionAdapter.selectedSubgoalIndex = goalViewModel.selectedSubGoal.value?.first
            actionAdapter.setData(actions)
        })

        goalViewModel.subgoals.observe(this, Observer { subgoals ->
            updateList(subgoals)
        })

        goalViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })

        goalViewModel.cancelButtonClicked.observe(this, Observer {
            showExitDialog()
        })

        goalViewModel.goalSavedSuccessfully.observe(this, Observer {
            navigation?.goBack()
        })
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_fragment_skyscraper_title)
            .setMessage(R.string.dialog_fragment_skyscraper_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                navigation?.goBack()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun updateList(subgoals: Map<Int, SubGoal>) {
        val subgoalsArray = Array(SUBGOALS_UPPER_BOUND + 1) { SubGoal("") }
        subgoals.entries.forEach { subgoalsArray[it.key] = it.value }
        subgoalAdapter.setData(subgoalsArray.toList())
    }

    private fun calculatePositionAvatar() {
        val targetLocation =
            binding.skyscraperCreateGoal.findViewWithTag<ImageView>(goalViewModel.goal.value!!.currentPositionAvatar.toString())
        targetLocation?.post { // This code will run when view created and rendered on screen
            val position = targetLocation.tag.toString().toInt()
            val extra = if (position % 2 != 0) targetLocation.height / 2 else 0
            binding.dragAvatar.y = targetLocation.y + targetLocation.height - binding.dragAvatar.height + extra
            binding.dragAvatar.x =
                targetLocation.x + (targetLocation.width / 2 - binding.dragAvatar.width / 2).toFloat()
        }
    }

    private fun startListeners() {
        binding.btnSubgoalSave.setOnClickListener {
            it.isFocusable = true
            it.requestFocus()
            goalViewModel.saveSubGoal()
            it.isFocusable = false
        }

        binding.btnAddAction.setOnClickListener {
            it.isFocusable = true
            it.requestFocus()
            goalViewModel.addNewAction()
            it.isFocusable = false
        }

        val seekbarChangeListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                goalViewModel.setPositionAvatar(if (progress < 0) 0 else progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
        binding.skyscraperElevatorSeekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        binding.skyscraperRopeSeekbar.setOnSeekBarChangeListener(seekbarChangeListener)

        // configuring the dropping places of the avatar
        binding.dragAvatar.setOnTouchListener(AvatarOnTouchListener())

        for (x in -2..goalViewModel.numberOfFloorsUpperBound) {
            binding.skyscraperCreateGoal.findViewWithTag<ImageView>(x.toString())
                ?.setOnDragListener(AvatarOnDragListener(object :
                    AvatarOnDragListener.AvatarDroppedListener {
                    override fun onAvatarDropped(floor: Int) {
                        goalViewModel.setPositionAvatar(floor)
                    }
                }, binding.dragAvatar))
        }
    }

    private fun setupRecyclerView() {
        // configuration of recyclerview for the goals
        val subgoalSelectedListener = object : SubGoalListener {
            override fun onSubGoalDismiss(position: Int) {
                goalViewModel.removeSubGoal(position)
            }

            override fun onSubGoalMove(fromPosition: Int, toPosition: Int) {
                goalViewModel.moveSubGoal(fromPosition, toPosition)
            }

            override fun onSubGoalSelected(position: Int) {
                goalViewModel.onSelectSubGoal(position)
            }
        }
        subgoalAdapter = SubGoalAdapter(
            goalViewModel.goal.value!!.goalColor,
            subgoalSelectedListener,
            // calculates the width of a floor : hoogte scherm * 0.49 (skyscraper neemt 49% van de hoogte in). Di in pixels en moet omgezet worden naar dp, vandaar vermenigvuldig met density
            ((Resources.getSystem().displayMetrics.heightPixels * 0.49 - (100 * Resources.getSystem().displayMetrics.density)) / (SUBGOALS_UPPER_BOUND + 1)).toInt()
        )
        val subGoalcallback: ItemTouchHelper.Callback =
            ItemTouchHelperCallback(subgoalAdapter, requireContext())
        val subGoaltouchHelper = ItemTouchHelper(subGoalcallback)
        subGoaltouchHelper.attachToRecyclerView(binding.skyscraperRvSubgoals)
        binding.skyscraperRvSubgoals.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.skyscraperRvSubgoals.adapter = subgoalAdapter

        // configuration of recyclerview for the actions of the selected goal
        val actionListener = object : ActionListener {
            override fun onActionDismiss(position: Int) {
                goalViewModel.removeAction(position)
            }

            override fun onActionMove(fromPosition: Int, toPosition: Int) {
                goalViewModel.moveAction(fromPosition, toPosition)
            }

            override fun onActionUpdated(position: Int, description: String) {
                goalViewModel.updateAction(position, description)
            }

            override fun onActionUpdateState(position: Int) {
                goalViewModel.updateActionState(position)
            }
        }
        actionAdapter = ActionAdapter(actionListener)
        val actionCallback: ItemTouchHelper.Callback =
            ItemTouchHelperCallback(actionAdapter, requireContext())
        val actionTouchHelper = ItemTouchHelper(actionCallback)
        actionTouchHelper.attachToRecyclerView(binding.rvGoalActions)
        binding.rvGoalActions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGoalActions.adapter = actionAdapter
    }

    private fun setThumb(avatarName: String) {
        // get the resource file
        val resID = avatarProvider.getAvatarDrawableStaanId(avatarName)
        // Get the size of the image
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER") var avatarbitmap =
            BitmapFactory.decodeResource(resources, resID, bmOptions)
        val avatarWitdh = bmOptions.outWidth
        val avatarHeight = bmOptions.outHeight
        // Set bitmap options to scale the image decode target
        bmOptions.inJustDecodeBounds = false

        // get the image for the elevator
        val borderId = resources.getIdentifier(
            "skyscraper_elevator_borders",
            "drawable",
            requireContext().packageName
        )
        var elevatorBitmap = BitmapFactory.decodeResource(resources, borderId)
        // canvas needs mutable bitmap
        elevatorBitmap = elevatorBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Figure out how to reduce the avatar
        // bmOptions.inSampleSize = min(avatarWitdh / THUMB_WIDTH, avatarHeight / THUMB_HEIGHT)
        bmOptions.inSampleSize = max(
            avatarWitdh / elevatorBitmap.width,
            (avatarHeight / (elevatorBitmap.height * 0.82)).toInt()
        )

        // scale the bitmap
        avatarbitmap = BitmapFactory.decodeResource(resources, resID, bmOptions)
        // set the avatar on the roof and dragavatar for elevator
        binding.skyscraperRoofAvatar.setImageBitmap(avatarbitmap)
        binding.dragAvatar.setImageBitmap(avatarbitmap)

        // place avatar in the elevator, 13dp is the height of the border of the elevator
        val canvas = Canvas(elevatorBitmap)
        // jjjj  canvas.drawBitmap(avatarbitmap, 0F, 13F, null)
        canvas.drawBitmap(
            avatarbitmap,
            (elevatorBitmap.width - avatarbitmap.width) / 2.toFloat(),
            (elevatorBitmap.height - (avatarbitmap.height + 13F)),
            null
        )

        // rotate the bitmaps for use in the vertical seekbars
        val matrix = Matrix()
        matrix.postRotate(90F)
        avatarbitmap = Bitmap.createBitmap(
            avatarbitmap, 0, 0,
            avatarbitmap.width, avatarbitmap.height, matrix, true
        )
        elevatorBitmap = Bitmap.createBitmap(
            elevatorBitmap,
            0,
            0,
            elevatorBitmap.width,
            elevatorBitmap.height,
            matrix,
            true
        )

        // create the drawable
        val drawableElevator: Drawable = BitmapDrawable(resources, elevatorBitmap)
        val drawable: Drawable = BitmapDrawable(resources, avatarbitmap)

        binding.skyscraperElevatorSeekbar.thumb = drawableElevator
        binding.skyscraperRopeSeekbar.thumb = drawable
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkyscraperNavigationListener) {
            navigation = context
        }
    }

    companion object {
        fun newInstance(goal: Goal): SkyscraperGoalFragment {
            return SkyscraperGoalFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(GOAL, goal)
                }
            }
        }
    }

    interface SkyscraperNavigationListener {
        fun goBack()
    }
}
