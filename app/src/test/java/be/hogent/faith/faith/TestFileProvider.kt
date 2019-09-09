package be.hogent.faith.faith

import be.hogent.faith.faith.util.TempFileProvider
import java.io.File

class TestFileProvider : TempFileProvider {
    override val tempPhotoFile: File
        get() = File("/tmp/testPhoto")
    override val tempAudioRecordingFile: File
        get() = File("/tmp/testAudio")
}