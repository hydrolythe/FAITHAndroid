package be.hogent.faith.service.usecases.cinema

import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

private const val FRAMERATE = 25

/**
 * Indicates how long a static image will be shown, in seconds
 */
private const val IMAGE_DISPLAY_TIME = 5

/**
 * An empty audio stream has to be added to inputs that don't have an audio stream themselves (e.g. images)
 */
private const val EMPTY_AUDIO_STREAM =
    "-t 0.1 -f lavfi -i anullsrc=channel_layout=stereo:sample_rate=48000"

/**
 * This class provides the ability to turn a collection of images and videos to be encoded into
 * a new video.
 *
 * Under the hood it uses FFMPEG. FFMPEG commands (or at least in this context) consist of these parts:
 * [inputs] [filtergraph] [streammap] [options] [outputFile]
 *
 * - [inputs] are specified with -i [pathToFile]. We also add [EMPTY_AUDIO_STREAM] so we can use it for
 * inputs that don't have an audio stream
 * - [filtergraph] lets you define filters for each input stream.
 * The filterchain for each inputstream is the parts between [i:v] and [vi], with i indicating the index in the list of arguments
 * All inputs use the scale filter to scale them up/down to the desired resolution, adding padding when needed.
 * Image inputs first go through a zoompan filter, with the duration set to [IMAGE_DISPLAY_TIME]*[FRAMERATE].
 * There is no actual zooming or panning being done, this is just to show the image for the specified number of frames.
 * The filtergraph ends with the concat operation, taking all the specified output video streams [vi] and their
 * audiostreams, or the empty audio stream when none is present, and concatenating them.
 *
 * - [streammap] defines the final output video and audio stream
 *
 * - [options] various options, such as the codec being used.
 *
 * Example command for encoding an image (img1.png) and two videos (2.mp4 and video1.mp4):
 * -i img1.png -i 2.mp4 -i video1.mp4 -t 0.1 -f lavfi -i anullsrc=channel_layout=stereo:sample_rate=48000 -filter_complex
 * "[0:v]zoompan=d=100,scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,setsar=1,fps=30,format=yuv420p[v0]
 * [1:v]scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,setsar=1,fps=30,format=yuv420p[v1]
 * [2:v]scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,setsar=1,fps=30,format=yuv420p[v2]
 * [v0][3:a][v1][3:a][v2][2:a]concat=n=3:v=1:a=1[v][a]"
 * -map "[v]" -map "[a]" -c:v libx264 -c:a aac -movflags +faststart output.mp4 -y
 */
class VideoEncoder {
    private var progressListener: EncodingProgressListener? = null

    /**
     * @param inputMedia An ordered list of files. Each file points to either a picture or a video.
     * These will be placed in the resulting video in the order they appear in the list.
     *
     * @param resolution the resolution for the output video
     *
     * @return a file containing the resulting video
     */
    fun encode(
        inputMedia: List<File>,
        resolution: Resolution = Resolution(1280, 720),
        listener: EncodingProgressListener? = null
    ): File {
        listener?.let { progressListener = it }

        val outputFile = File.createTempFile("createdVideo", ".mp4")
        val sources = inputMedia.map { SourceMaterial(it, getMediaType(it)) }

        val inputs = createInputsCommandPart(inputMedia)
        val filterCommand = filterCommand(sources, resolution)

        val fullCommand =
            "$inputs $EMPTY_AUDIO_STREAM $filterCommand -map \"[v]\" -map \"[a]\" -c:v libx264 -c:a aac -movflags +faststart ${outputFile.path} -y"

        startCallback(calculateTotalDuration(sources))
        Log.d("VideoEncoder", "Command: $fullCommand")
        FFmpeg.execute(fullCommand)
        stopCallback()
        return outputFile
    }

    /**
     * @return the duration of the concatenated video, in seconds
     */
    private fun calculateTotalDuration(sources: List<SourceMaterial>): Int {
        return sources.fold(0) { acc, sourceMaterial ->
            when (sourceMaterial.mediaType) {
                MediaType.PICTURE -> acc + IMAGE_DISPLAY_TIME
                MediaType.VIDEO -> acc + getDuration(sourceMaterial)
            }
        }
    }

    private fun getDuration(sourceMaterial: VideoEncoder.SourceMaterial): Int {
        val info = FFprobe.getMediaInformation(sourceMaterial.file.path)
        return (info.duration / 1_000f).roundToInt()
    }

    private fun concatCommand(sources: List<SourceMaterial>): String {
        val inputPipes = sources.foldIndexed("") { index, acc, source ->
            // The empty audio input is the one that's added last, so its index is sources.size
            val audioPipe = if (hasAudioStream(source.file)) "[$index:a]" else "[${sources.size}:a]"
            "$acc[v$index]$audioPipe"
        }
        return "${inputPipes}concat=n=${sources.size}:v=1:a=1[v][a]"
    }

    private fun filterCommand(
        sources: List<SourceMaterial>,
        resolution: Resolution
    ): String {
        val filterGraph = sources.mapIndexed { index, source ->
            val inputPipe = "[$index:v]"
            val outputPipe = "[v$index]"

            val imageToVideoFilter =
                if (source.mediaType == MediaType.PICTURE) "zoompan=d=${IMAGE_DISPLAY_TIME * FRAMERATE}," else ""

            val scaleFilter =
                "scale=${resolution.width}:${resolution.height}:force_original_aspect_ratio=decrease,pad=${resolution.width}:${resolution.height}:(ow-iw)/2:(oh-ih)/2,setsar=1,fps=$FRAMERATE,format=yuv420p"

            "$inputPipe$imageToVideoFilter$scaleFilter$outputPipe;"
        }.joinToString(separator = "")

        val mapCommand = concatCommand(sources)

        return "-filter_complex \"$filterGraph $mapCommand\""
    }

    private fun createInputsCommandPart(videos: List<File>): String {
        return videos.fold("", { acc, file -> acc + "-i ${file.path} " }).trim()
    }

    private fun stopCallback() {
        // Indicate completion
        progressListener?.onProgressChanged(100)
        Config.resetStatistics()
    }

    private fun startCallback(durationInSeconds: Int) {
        Config.enableStatisticsCallback { statistics ->
            statistics?.let {
                val totalFrames = durationInSeconds * FRAMERATE
                val currentProgress = statistics.videoFrameNumber.toDouble() / totalFrames
                val currentProgressPercent = (currentProgress * 100).toInt()
                progressListener?.onProgressChanged(currentProgressPercent)
            }
        }
    }

    interface EncodingProgressListener {
        fun onProgressChanged(progress: Int)
    }

    class Resolution(
        val width: Int,
        val height: Int
    )

    private data class SourceMaterial(val file: File, val mediaType: MediaType)

    private enum class MediaType {
        VIDEO,
        PICTURE
    }

    private fun hasAudioStream(file: File): Boolean {
        return FFprobe.getMediaInformation(file.absolutePath).streams.any { it.type == "audio" }
    }

    private fun getMediaType(file: File): MediaType {
        val info = FFprobe.getMediaInformation(file.absolutePath)
        Timber.d("${file.path} has mediatype ${info.format}")
        val format = when {
            info.format == "image2" -> MediaType.PICTURE
            info.format == "jpeg_pipe" -> MediaType.PICTURE
            info.format == "png_pipe" -> MediaType.PICTURE
            info.format == "avi" -> MediaType.VIDEO
            info.format.contains("mp4") -> MediaType.VIDEO
            else -> throw IllegalArgumentException("File ${file.path} is not a valid media type to add to a video")
        }
        Timber.d("Resulted in type $format")
        return format
    }
}
