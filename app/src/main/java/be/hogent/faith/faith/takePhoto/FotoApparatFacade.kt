package be.hogent.faith.faith.takePhoto

import be.hogent.faith.service.usecases.interfaces.PhotoTaker
import io.fotoapparat.Fotoapparat
import io.fotoapparat.result.adapter.rxjava2.toCompletable
import io.reactivex.Completable
import java.io.File

class FotoApparatFacade(private val fotoapparat: Fotoapparat) : PhotoTaker {
    override fun takePhoto(saveFile: File): Completable {
        return fotoapparat.takePicture().saveToFile(saveFile).toCompletable()
    }
}