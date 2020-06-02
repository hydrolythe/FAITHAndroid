package be.hogent.faith.faith.detailscontainer

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsMetaDataViewModel
import org.threeten.bp.LocalDate
import kotlin.reflect.KClass

abstract class SaveDetailsContainerDetailDialog(
    protected val detailType: KClass<Detail>
) : DialogFragment() {

    protected lateinit var saveDetailBinding: ViewDataBinding
    protected abstract val detailsMetadataViewModel: DetailsMetaDataViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), R.style.Dialog_NearlyFullScreen).apply {
            setStyle(STYLE_NO_TITLE, 0)
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        initTitle()
    }

    private fun initTitle() {
        detailsMetadataViewModel.header.value = when (detailType.qualifiedName) {
            "be.hogent.faith.domain.models.detail.DrawingDetail" -> getString(R.string.je_tekening_opslaan)
            "be.hogent.faith.domain.models.detail.PhotoDetail" -> getString(R.string.je_foto_opslaan)
            "be.hogent.faith.domain.models.detail.VideoDetail" -> getString(R.string.je_extern_bestand_opslaan)
            "be.hogent.faith.domain.models.detail.AudioDetail" -> getString(R.string.je_audio_opslaan)
            "be.hogent.faith.domain.models.detail.YoutubeVideoDetail" -> getString(R.string.je_extern_bestand_opslaan)
            "be.hogent.faith.domain.models.detail.TextDetail" -> getString(R.string.je_tekst_opslaan)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun startListeners() {
        detailsMetadataViewModel.detailDateButtonClicked.observe(this, Observer {
            val now = detailsMetadataViewModel.detailDate.value!!
            val dateSetListener =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    detailsMetadataViewModel.detailDate.postValue(
                        LocalDate.of(year, monthOfYear, dayOfMonth)
                    )
                }
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                now.year,
                now.monthValue,
                now.dayOfMonth
            ).show()
        })

        detailsMetadataViewModel.cancelClicked.observe(this, Observer {
            dismiss()
        })

        detailsMetadataViewModel.detailMetaDataSet.observe(this, Observer {
            val detailFragment: DetailFragment<Detail>? =
                targetFragment as DetailFragment<Detail>?
            detailFragment?.onFinishSaveDetailsMetaData(
                detailsMetadataViewModel.detailTitle.value!!,
                detailsMetadataViewModel.detailDate.value!!.atStartOfDay()
            )
            Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT)
                .show()
            dismiss()
        })
    }
}