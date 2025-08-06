package icu.samnyan.aqua.net.components

import ext.*
import icu.samnyan.aqua.net.db.AquaNetUser
import icu.samnyan.aqua.net.utils.PathProps
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeBytes

@Configuration
@ConfigurationProperties(prefix = "aqua-net.image-processing")
class ImageProcessingProperties {
    var enabled: Bool = false
    lateinit var baseUrl: Str
}

@Service
class ImageProcessing(
    val props: ImageProcessingProperties,
    val paths: PathProps
) {
    val log = LoggerFactory.getLogger(ImageProcessing::class.java)!!

    var tempPortraitPath: Path = paths.aquaNetTempPortrait.path()
    val portraitPath: Path = paths.aquaNetPortrait.path()
    var tempBackgroundPath: Path = paths.aquaNetTempBackground.path()
    val backgroundPath: Path = paths.aquaNetBackground.path()

    /*
    suspend fun generate(tempFileName: Str, target: Path, details: Str) {
        // In the future, implementing a queue might be necessary.
        // For now, this'll do, I think.
        val url = props.baseUrl + "/unsafe/" + details + tempFileName
        val response = HTTP.get(url).bodyAsBytes()
        target.writeBytes(response)
    }

    suspend fun generateProfilePicture(user: AquaNetUser, bytes: ByteArray, extension: Str) {
        val tempFileName = user.auId.toString() + extension
        val tempPath = (tempPortraitPath / tempFileName)
        tempPath.writeBytes(bytes)
        generate(
            tempFileName,
            (portraitPath / (user.auId.toString() + ".webp")),
            "256x256/filters:format(webp)/portrait/"
        )
        tempPath.deleteIfExists()
    }

    suspend fun generateProfileBackground(user: AquaNetUser, bytes: ByteArray, extension: Str) {
        val tempFileName = user.auId.toString() + extension
        val tempPath = (tempBackgroundPath / tempFileName)
        tempPath.writeBytes(bytes)
        generate(
            tempFileName,
            (backgroundPath / (user.auId.toString() + ".webp")),
            "trim/fit-in/1920x1920/filters:upscale():blur(24):format(webp)/background/"
        )
        tempPath.deleteIfExists()
    }*/

    suspend fun suspendImage(file: MultipartFile, user: AquaNetUser, path: Path): Path? {
        val bytes = file.bytes
        val mime = TIKA.detect(bytes) ?: (400 - "Invalid file type")
        val extension = MIMES.forName(mime)?.extension

        if (!mime.startsWith("image/") || extension == null) 400 - "Invalid file type"

        val path = (path / (user.auId.toString() + extension))
        path.writeBytes(bytes)

        return path
    }

    suspend fun processImage(info: Str, path: Path) {
        val url = props.baseUrl + "/unsafe/" + info
        val response = HTTP.get(url).bodyAsBytes()
        path.writeBytes(response)
    }
}