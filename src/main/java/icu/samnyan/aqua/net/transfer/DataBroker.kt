package icu.samnyan.aqua.net.transfer

import ext.*
import icu.samnyan.aqua.sega.chusan.model.request.UpsertUserAll
import icu.samnyan.aqua.sega.chusan.model.userdata.UserActivity
import icu.samnyan.aqua.sega.chusan.model.userdata.UserItem
import icu.samnyan.aqua.sega.chusan.model.userdata.UserMusicDetail
import icu.samnyan.aqua.sega.util.jackson.StringMapper


interface DataBroker {
    fun pull(): String
}


class ChusanDataBroker(val allNet: AllNetClient, val log: (String) -> Unit): DataBroker {
    val mapper = StringMapper()
    val url by lazy { "${allNet.gameUrl.ensureEndingSlash()}ChuniServlet" }

    inline fun <reified T> String.get(key: String, data: JDict) = "$url/$this".request()
        .postZ(mapper.write(data))
        .bodyMaybeZ()
        .jsonMap()[key]
        .let { mapper.convert<T>(it) }
        .also {
            if (it is List<*>) log("✅ $this: ${it.size}")
            else log("✅ $this")
        }

    class UserMusicWrapper(var userMusicDetailList: List<UserMusicDetail>)

    override fun pull(): String {
        log("Game URL: ${allNet.gameUrl}")
        log("User ID: ${allNet.userId}")

        val userId = mapOf("userId" to allNet.userId)
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