package icu.samnyan.aqua.net.transfer

import ext.API
import ext.RB
import ext.logger
import ext.toJson
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
        mapOf("error" to e.message)
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

    @API("/export")
    fun export(@RB allNet: AllNetClient, response: HttpServletResponse) {
        val stream = response.initStream()
        try {
            log.info("Transfer export: $allNet")
            stream.log("Starting export...")
            val broker = allNet.findDataBroker { stream.log(it) }
            val out = broker.pull()
            stream.log("Export complete")
            stream.sendJson(mapOf("data" to out))
        } catch (e: Exception) {
            log.error("Transfer export error", e)
            stream.sendJson(mapOf("error" to e.message))
        } finally {
            stream.close()
        }
    }
}