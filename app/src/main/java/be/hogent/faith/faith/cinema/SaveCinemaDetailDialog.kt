package be.hogent.faith.faith.cinema

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogCinemaSaveDetailBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveCinemaDetailDialog(private var detail: Detail) : DialogFragment() {
    private lateinit var saveDetailBinding: DialogCinemaSaveDetailBinding

    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel()

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    companion object {
        fun newInstance(detail: Detail): SaveCinemaDetailDialog {
            return SaveCinemaDetailDialog(detail)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), R.style.Dialog_NearlyFullScreen).apply {
            setStyle(STYLE_NO_TITLE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cinemaOverviewViewModel.resetDetails()
        saveDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_cinema_save_detail, container, false)
        saveDetailBinding.cinemaViewModel = cinemaOverviewViewModel
        saveDetailBinding.lifecycleOwner = this
        return saveDetailBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        initTitle()
    }

    private fun initTitle() {
        saveDetailBinding.saveCinemadetailTitle.text = when (detail) {
            is DrawingDetail -> getString(R.string.je_tekening_opslaan)
            is PhotoDetail -> getString(R.string.je_foto_opslaan)
            is VideoDetail -> getString(R.string.je_extern_bestand_opslaan)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun startListeners() {
        cinemaOverviewViewModel.detailDateButtonClicked.observe(this, Observer {
            CinemaDateDialog.newInstance().show(requireActivity().supportFragmentManager, null)
        })

        saveDetailBinding.btnCinemadetailSave.setOnClickListener {
            cinemaOverviewViewModel.onSaveClicked(
                cinemaOverviewViewModel.detailTitle.value!!,
                userViewModel.user.value!!,
                detail,
                cinemaOverviewViewModel.detailDate.value!!
            )
        }

        cinemaOverviewViewModel.detailIsSaved.observe(this, Observer {
            Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT)
                .show() // Mag deze algemeen blijven of best per detail ?
            dismiss()
        })

        saveDetailBinding.btnCinemaDetailCancel.setOnClickListener {
            dismiss()
            cinemaOverviewViewModel.goToDetail(detail)
        }

        cinemaOverviewViewModel.errorMessage.observe(this, Observer {
            if (it != null)
                Toast.makeText(context, resources.getString(it), Toast.LENGTH_LONG).show()
        })
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        cinemaOverviewViewModel.goToDetail(detail)
    }
}