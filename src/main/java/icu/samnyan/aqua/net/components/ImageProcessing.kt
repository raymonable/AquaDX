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