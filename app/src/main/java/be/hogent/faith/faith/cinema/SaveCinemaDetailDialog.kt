package be.hogent.faith.faith.cinema

import android.app.Dialog
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
import be.hogent.faith.faith.details.DetailsMetaDataStrategy
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveCinemaDetailDialog(private val detailsMetaDataStrategy: DetailsMetaDataStrategy) : DialogFragment() {
    private lateinit var saveDetailBinding: DialogCinemaSaveDetailBinding
    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel()

    companion object {
        fun newInstance(detailsMetaDataStrategy: DetailsMetaDataStrategy): SaveCinemaDetailDialog {
            return SaveCinemaDetailDialog(detailsMetaDataStrategy)
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
        saveDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_cinema_save_detail, container, false)
        saveDetailBinding.cinemaViewModel = detailsMetaDataStrategy
        saveDetailBinding.lifecycleOwner = this
        return saveDetailBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        initTitle()
    }

    private fun initTitle() {
        /*
        saveDetailBinding.saveCinemadetailTitle.text = when (detail) {
            is DrawingDetail -> getString(R.string.je_tekening_opslaan)
            is PhotoDetail -> getString(R.string.je_foto_opslaan)
            is VideoDetail -> getString(R.string.je_extern_bestand_opslaan)
            else -> throw UnsupportedOperationException()
        }
        */
    }

    private fun startListeners() {
        detailsMetaDataStrategy.detailDateButtonClicked.observe(this, Observer {
            CinemaDateDialog.newInstance().show(requireActivity().supportFragmentManager, null)
        })

/*        saveDetailBinding.btnCinemadetailSave.setOnClickListener {
            detailsMetaDataStrategy.onSaveClicked(
                cinemaOverviewViewModel.detailTitle.value!!,
                userViewModel.user.value!!,
                detail,
                cinemaOverviewViewModel.detailDate.value!!
            )
        }
        */

        detailsMetaDataStrategy.detailMetaDataSet.observe(this, Observer {
            Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT)
                .show() // Mag deze algemeen blijven of best per detail ?
            dismiss()
        })

        saveDetailBinding.btnCinemaDetailCancel.setOnClickListener {
            dismiss()
        }

        cinemaOverviewViewModel.errorMessage.observe(this, Observer {
            if (it != null)
                Toast.makeText(context, resources.getString(it), Toast.LENGTH_LONG).show()
        })
    }
}