package be.hogent.faith.faith.skyscraper.goal

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperGoalBinding
import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.ReachGoalWay
import be.hogent.faith.domain.models.goals.SUBGOALS_UPPER_BOUND
import be.hogent.faith.domain.models.goals.SubGoal
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.util.factory.GoalFactory
import kotlinx.android.synthetic.main.fragment_skyscraper_goal.skyscraper_elevator_seekbar
import kotlinx.android.synthetic.main.fragment_skyscraper_goal.skyscraper_roof_avatar
import kotlinx.android.synthetic.main.fragment_skyscraper_goal.skyscraper_rope_seekbar
import kotlinx.android.synthetic.main.fragment_skyscraper_goal.skyscraper_stairs_seekbar
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class SkyscraperGoalFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperGoalBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    // TODO inject the selected goal
    private val goalViewModel: GoalViewModel by inject { parametersOf(GoalFactory.makeGoal(5)) }
    private lateinit var actionAdapter: ActionAdapter
    private lateinit var subgoalAdapter: SubGoalAdapter
    private val avatarProvider: AvatarProvider by inject()

    val list = arrayListOf<Action>()
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
        setupRecyclerView()
        setThumb(userViewModel.user.value!!.avatarName)
        startListeners()
        startViewModelListeners()
    }

    private fun startViewModelListeners() {
        goalViewModel.goal.observe(this, Observer {
            skyscraper_roof_avatar.visibility = if (it.isCompleted) View.VISIBLE else View.GONE
            skyscraper_elevator_seekbar.progress = it.currentPositionAvatar
            skyscraper_elevator_seekbar.visibility =
                if (!it.isCompleted && it.chosenReachGoalWay == ReachGoalWay.Elevator) View.VISIBLE else View.GONE
            skyscraper_rope_seekbar.progress = it.currentPositionAvatar
            skyscraper_rope_seekbar.visibility =
                if (!it.isCompleted && it.chosenReachGoalWay == ReachGoalWay.Rope) View.VISIBLE else View.GONE
            skyscraper_stairs_seekbar.progress = it.currentPositionAvatar
            skyscraper_stairs_seekbar.visibility =
                if (!it.isCompleted && it.chosenReachGoalWay == ReachGoalWay.Stairs) View.VISIBLE else View.GONE
        })
        // Update adapter when event changes
        goalViewModel.actions.observe(this, Observer { actions ->
            Timber.i("actions are passed to adapter")
            actionAdapter.submitList(actions)
            actionAdapter.notifyDataSetChanged()
        })

        goalViewModel.subgoals.observe(this, Observer { subgoals ->
            Timber.i("subgoals are passed to adapter")
            val subgoalsArray = Array<SubGoal>(SUBGOALS_UPPER_BOUND + 1) { _ -> SubGoal("") }
            subgoals.entries.forEach { subgoalsArray[it.key] = it.value }
            subgoalAdapter.submitList(subgoalsArray.toList())
            subgoalAdapter.notifyDataSetChanged()
        })

        goalViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })

        goalViewModel.cancelButtonClicked.observe(this, Observer {
            navigation?.goBack()
        })

        goalViewModel.goalSavedSuccessfully.observe(this, Observer {
            navigation?.goBack()
        })
    }

    private fun startListeners() {
        val seekbarChangeListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                goalViewModel.setPositionAvatar(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Timber.i("tracking seekbar started")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
        skyscraper_elevator_seekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        skyscraper_rope_seekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        skyscraper_stairs_seekbar.setOnSeekBarChangeListener(seekbarChangeListener)
    }

    private fun setupRecyclerView() {
        // configuration of recyclerview for the goals
        val subgoalSelectedListener = object : SubGoalSelectedListener {
            override fun onSubGoalSelected(position: Int) {
                goalViewModel.onSelectSubGoal(position)
            }
        }
        subgoalAdapter = SubGoalAdapter(
            subgoalSelectedListener,
            ((Resources.getSystem()
                .getDisplayMetrics().heightPixels * 0.49 - (100 * Resources.getSystem()
                .getDisplayMetrics().density)) / (SUBGOALS_UPPER_BOUND + 1)).toInt()
        )
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
        }
        actionAdapter = ActionAdapter(actionListener)
        val callback: ItemTouchHelper.Callback =
            ActionTouchHelperCallback(actionAdapter, requireContext())
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvGoalActions)
        binding.rvGoalActions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGoalActions.adapter = actionAdapter
    }

    private fun setThumb(avatarName: String) {
        val targetWidth = 24
        val targetHeight = 55
        val resID = avatarProvider.getAvatarDrawableStaanId(avatarName)

        /* Get the size of the image */
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeResource(resources, resID, bmOptions)
        val avatarWitdh = bmOptions.outWidth
        val avatarHeight = bmOptions.outHeight

        /* Figure out which way needs to be reduced less */
        var scaleFactor = 1
        if (targetWidth > 0 || targetHeight > 0) {
            scaleFactor = Math.min(avatarWitdh / targetWidth, avatarHeight / targetHeight)
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds =
            false
        bmOptions.inSampleSize = scaleFactor
        //  bmOptions.inPurgeable = true
        // schall de bitmap
        bitmap = BitmapFactory.decodeResource(resources, resID, bmOptions)
        skyscraper_roof_avatar.setImageBitmap(bitmap)
        // roteer de bitmap
        val matrix = Matrix()
        matrix.postRotate(90F)
        bitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true
        )
        // als met lift
        /*
        stel thumb is beeld lift
           val canvas = Canvas(thumb)

           canvas.drawBitmap(
               bitmap, Rect(0, 0, bitmap.width, bitmap.height),
               Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null)
                       val drawable: Drawable = BitmapDrawable(resources, thumb)
                               seekbar.setThumb(drawable)
                 */
        val drawable: Drawable = BitmapDrawable(resources, bitmap)

        skyscraper_elevator_seekbar.setThumb(drawable)
        skyscraper_stairs_seekbar.setThumb(drawable)
        skyscraper_rope_seekbar.setThumb(drawable)
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
