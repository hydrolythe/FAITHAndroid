package be.hogent.faith.faith.createUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCreateEventBinding
import org.koin.android.viewmodel.ext.android.viewModel

class CreateEventFragment : Fragment() {

    private val createEventViewModel: CreateEventViewModel by viewModel()
    private lateinit var createEventBinding: FragmentCreateEventBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        createEventBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_event, container, false)
        createEventBinding.createUserVM = createEventViewModel
        createEventBinding.setLifecycleOwner(this)
        return createEventBinding.root
    }
}