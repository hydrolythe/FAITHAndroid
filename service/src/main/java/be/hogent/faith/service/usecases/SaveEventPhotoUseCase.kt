package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class SaveEventPhotoUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventPhotoUseCase.Params>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        val saveFileName = createSaveFileName()
        return Completable.fromSingle(
            storageRepository.storeBitmap(
                params.bitmap,
                params.event,
                saveFileName
            ).doOnSuccess {
                params.event.addDetail(Detail(DetailType.PICTURE, it))
            }
        )
    }

    /**
     * Returns a saveFile with the name in the following format:
     * day_month_year_hour_minute_second_millis
     */
    private fun createSaveFileName(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("d_M_y_H_m_s_A"))
    }

    data class Params(val bitmap: Bitmap, val event: Event)
}