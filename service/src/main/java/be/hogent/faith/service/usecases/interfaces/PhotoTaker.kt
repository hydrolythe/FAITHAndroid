package be.hogent.faith.service.usecases.interfaces

import io.reactivex.Completable
import java.io.File

interface PhotoTaker {
    /**
     * Take a picture and save it to the provided file
     */
    fun takePhoto(saveFile: File): Completable
}