package icu.samnyan.aqua.sega.chusan

import ext.header
import ext.post
import ext.request
import icu.samnyan.aqua.api.model.resp.sega.chuni.v2.external.Chu3DataExport
import icu.samnyan.aqua.sega.util.AllNetBillingDecoder


val keychipPattern = Regex("([A-Z\\d]{4}-[A-Z\\d]{11}|[A-Z\\d]{11})")

class AllNetHost(val dns: String, val keychip: String, val game: String, val version: String, val card: String) {
    init {
        // Check if keychip is valid
        // TODO : Use a more appropriate exception
        if (!keychipPattern.matches(keychip)) throw Exception("Invalid keychip")
    }

    val requestKeychip by lazy {
        // A123-45678901337 -> A1234567890
        if (keychip.length == 11) keychip
        else keychip.substring(0, 4) + keychip.substring(5, 11)
    }

    // Send AllNet PowerOn request to obtain game URL
    val gameUrl by lazy {
        "$dns/sys/servlet/PowerOn".request()
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .post(AllNetBillingDecoder.encodeAllNet(mapOf(
                "game_id" to game,
                "ver" to version,
                "serial" to requestKeychip,
                "ip" to "127.0.0.1", "firm_ver" to "60001", "boot_ver" to "0000",
                "encode" to "UTF-8", "format_ver" to "3", "hops" to "1", "token" to "2864179931"
            )))
            .body()
            .split("&")
            .map { it.split("=") }
            .filter { it.size == 2 }
            .associate { it[0] to it[1] }["uri"]
            ?: throw Exception("PowerOn Failed: No game URL returned")
    }
}
class ChusanDataBroker {

    fun pull(host: AllNetHost): Chu3DataExport {
        // Send AllNet PowerOn request to obtain game URL

        return Chu3DataExport()
    }


}
