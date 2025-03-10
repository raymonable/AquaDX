package icu.samnyan.aqua.sega.chusan

import ext.*
import icu.samnyan.aqua.sega.aimedb.AimeDbClient
import icu.samnyan.aqua.sega.aimedb.AimeDbClient.Companion.sendAimePacket
import icu.samnyan.aqua.sega.chusan.model.request.UpsertUserAll
import icu.samnyan.aqua.sega.chusan.model.userdata.UserActivity
import icu.samnyan.aqua.sega.chusan.model.userdata.UserItem
import icu.samnyan.aqua.sega.chusan.model.userdata.UserMusicDetail
import icu.samnyan.aqua.sega.util.AllNetBillingDecoder
import icu.samnyan.aqua.sega.util.jackson.StringMapper
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor


val keychipPattern = Regex("([A-Z\\d]{4}-[A-Z\\d]{11}|[A-Z\\d]{11})")

class AllNetHost(val dns: String, val keychip: String, val game: String, val version: String, val card: String) {
    init {
        // Check if keychip is valid
        // TODO : Use a more appropriate exception
        if (!keychipPattern.matches(keychip)) throw Exception("Invalid keychip")
    }

    val keychipShort by lazy {
        // A123-45678901337 -> A1234567890
        if (keychip.length == 11) keychip
        else keychip.substring(0, 4) + keychip.substring(5, 11)
    }
    val aime by lazy { AimeDbClient(game, keychipShort) }

    // Send AllNet PowerOn request to obtain game URL
    val gameUrl by lazy {
        "$dns/sys/servlet/PowerOn".request()
            .header("Content-Type" to "application/x-www-form-urlencoded")
            .post(AllNetBillingDecoder.encodeAllNet(mapOf(
                "game_id" to game,
                "ver" to version,
                "serial" to keychipShort,
                "ip" to "127.0.0.1", "firm_ver" to "60001", "boot_ver" to "0000",
                "encode" to "UTF-8", "format_ver" to "3", "hops" to "1", "token" to "2864179931"
            )))
            .bodyString()
            .split("&")
            .map { it.split("=") }
            .filter { it.size == 2 }
            .associate { it[0] to it[1] }["uri"]
            ?: throw Exception("PowerOn Failed: No game URL returned")
    }

    val userId by lazy {
        when (card.length) {
            20 -> aime.createReqLookupV2(card)
            16 -> aime.createReqFelicaLookupV2(card)
            else -> throw Exception("Invalid card. Please input either 20-digit numeric access code (e.g. 5010000...0) or 16-digit hex Felica ID (e.g. 012E123456789ABC).")
        }.sendAimePacket(dns.substringAfter("://")).getLongLE(0x20)
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMusicWrapper {
    var userMusicDetailList: List<UserMusicDetail> = emptyList()
}

class ChusanDataBroker(val host: AllNetHost) {
    val mapper = StringMapper()
    val url by lazy { "${host.gameUrl.ensureEndingSlash()}ChuniServlet" }
    val log = logger()

    inline fun <reified T> String.get(key: String, data: JDict) = "$url/$this".request()
        .postZ(mapper.write(data))
        .bodyMaybeZ()
        .jsonMap()[key]
        .let { mapper.convert<T>(it) }
        .also {
            if (it is List<*>) log.info("✅ $this: ${it.size}")
            else log.info("✅ $this")
        }

    fun pull(): String {
        log.info("Game URL: ${host.gameUrl}")
        log.info("User ID: ${host.userId}")

        val userId = mapOf("userId" to host.userId)
        val paged = userId + mapOf("nextIndex" to 0, "maxCount" to 10000000)

        return mapper.write(UpsertUserAll().apply {
            userData = ls("GetUserDataApi".get("userData", userId))
            userGameOption = ls("GetUserOptionApi".get("userGameOption", userId))
            userCharacterList = "GetUserCharacterApi".get("userCharacterList", paged)
            userActivityList = (1..5).flatMap {
                "GetUserActivityApi".get<List<UserActivity>>("userActivityList", userId + mapOf("kind" to it))
            }
            userItemList = (1..12).flatMap {
                "GetUserItemApi".get<List<UserItem>>(
                    "userItemList",
                    userId + mapOf("nextIndex" to 10000000000 * it, "maxCount" to 10000000)
                )
            }
            userRecentRatingList = "GetUserRecentRatingApi".get("userRecentRatingList", userId)
            userMusicDetailList = "GetUserMusicApi".get<List<UserMusicWrapper>>("userMusicList", paged)
                .flatMap { it.userMusicDetailList }
            userCourseList = "GetUserCourseApi".get("userCourseList", paged)
            userFavoriteMusicList = "GetUserFavoriteItemApi".get("userFavoriteItemList", paged + mapOf("kind" to 1))
            // TODO userMapAreaList = "GetUserMapAreaApi"
            // TODO userNetBattleData = ls("GetUserNetBattleDataApi".get("userNetBattleData", userId))
            userUnlockChallengeList = "GetUserUCApi".get("userUnlockChallengeList", userId)
        })
    }
}
