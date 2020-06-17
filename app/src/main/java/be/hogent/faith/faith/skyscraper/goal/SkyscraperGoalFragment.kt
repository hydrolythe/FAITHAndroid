package be.hogent.faith.faith.skyscraper.goal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Observer
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperGoalBinding
import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.util.factory.GoalFactory
import kotlinx.android.synthetic.main.fragment_skyscraper_goal.seekbar
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SkyscraperGoalFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperGoalBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    // TODO inject the selected goal
    private val goalViewModel: GoalViewModel by inject { parametersOf(GoalFactory.makeGoal(5)) }
    private lateinit var adapter: ActionAdapter
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
        startListeners()
    }

    private fun startListeners() {
        // Update adapter when event changes
        goalViewModel.actions.observe(this, Observer { actions ->
            adapter.submitList(actions)
            adapter.notifyDataSetChanged()
        })

        goalViewModel.cancelButtonClicked.observe(this, Observer {
            navigation?.goBack()
        })
    }

    private fun setupRecyclerView() {
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
        adapter = ActionAdapter(actionListener)
        val callback: ItemTouchHelper.Callback =
            ActionTouchHelperCallback(adapter, requireContext())
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvGoalActions)
        binding.rvGoalActions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGoalActions.adapter = adapter
        setThumb(userViewModel.user.value!!.avatarName)
    }

    private fun setThumb(avatarName: String) {
        val targetWidth = 50
        val targetHeight = 50
        val resID = resources.getIdentifier(
            avatarName,
            "drawable", context?.packageName
        )
        val matrix = Matrix()
        matrix.postRotate(90F)

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
        // roteer de bitmap
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
        seekbar.setThumb(drawable)
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
