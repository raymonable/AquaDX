package icu.samnyan.aqua.net.transfer

import ext.*
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RestController
import java.io.PrintWriter

@RestController
@API("/api/v2/transfer")
class TransferApis {
    val log = logger()

    @API("/check")
    fun check(@RB allNet: AllNetClient) = try {
        log.info("Transfer check: $allNet")
        mapOf("gameUrl" to allNet.gameUrl, "userId" to allNet.userId)
    } catch (e: Exception) {
        log.error("Transfer check error", e)
        400 - "Transfer check error: ${e.message}"
    }

    fun HttpServletResponse.initStream(): PrintWriter {
        contentType = "text/event-stream; charset=utf-8"
        characterEncoding = "UTF-8"
        return writer
    }

    fun PrintWriter.sendJson(m: Any) {
        println("data: ${m.toJson()}\n")
        flush()
    }

    fun PrintWriter.log(m: String) = sendJson(mapOf("message" to m))

    @API("/pull")
    fun pull(@RB allNet: AllNetClient, response: HttpServletResponse) {
        val stream = response.initStream()
        try {
            log.info("Transfer pull: $allNet")
            stream.log("Starting pull...")
            val broker = allNet.findDataBroker { stream.log(it) }
            val out = broker.pull()
            stream.log("Pull complete")
            stream.sendJson(mapOf("data" to out))
        } catch (e: Exception) {
            log.error("Transfer pull error", e)
            stream.sendJson(mapOf("error" to e.message))
        } finally {
            stream.close()
        }
    }

    @API("/push")
    fun push(@RB allNet: AllNetClient, data: String) = try {
        log.info("Transfer push: $allNet")
        val broker = allNet.findDataBroker { log.info(it) }
        broker.push(data)
        mapOf("status" to "ok")
    } catch (e: Exception) {
        log.error("Transfer push error", e)
        400 - "Transfer push error: ${e.message}"
    }
}