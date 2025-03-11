package icu.samnyan.aqua.net.transfer

import ext.*
import icu.samnyan.aqua.sega.chusan.model.request.Chu3UserAll
import icu.samnyan.aqua.sega.chusan.model.userdata.UserActivity
import icu.samnyan.aqua.sega.chusan.model.userdata.UserItem
import icu.samnyan.aqua.sega.chusan.model.userdata.UserMusicDetail
import icu.samnyan.aqua.sega.maimai2.model.request.Mai2UserAll
import icu.samnyan.aqua.sega.util.jackson.BasicMapper
import icu.samnyan.aqua.sega.util.jackson.IMapper
import icu.samnyan.aqua.sega.util.jackson.StringMapper


abstract class DataBroker(
    val allNet: AllNetClient,
    val log: (String) -> Unit,
) {
    abstract val mapper: IMapper
    abstract val url: String

    inline fun <reified T> String.get(key: String, data: JDict): T = "$url/$this".request()
        .postZ(mapper.write(data))
        .bodyMaybeZ()
        .jsonMap()[key]
        ?.let { mapper.convert<T>(it) }
        ?.also {
            if (it is List<*>) log("✅ $this: ${it.size}")
            else log("✅ $this")
        } ?: throw NullPointerException("❌ $this")

    abstract fun pull(): String
    fun push(data: String) {
        log("Pushing data")
        "UpsertUserAll".request().postZ(data).bodyMaybeZ().also { log(it) }
    }
}


class ChusanDataBroker(allNet: AllNetClient, log: (String) -> Unit): DataBroker(allNet, log) {
    override val mapper = StringMapper()
    override val url by lazy { "${allNet.gameUrl.ensureEndingSlash()}ChuniServlet" }

    class UserMusicWrapper(var userMusicDetailList: List<UserMusicDetail>)

    override fun pull(): String {
        log("Game URL: ${allNet.gameUrl}")
        log("User ID: ${allNet.userId}")

        val userId = mapOf("userId" to allNet.userId)
        val paged = userId + mapOf("nextIndex" to 0, "maxCount" to 10000000)

        return mapper.write(Chu3UserAll().apply {
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


class MaimaiDataBroker(allNet: AllNetClient, log: (String) -> Unit): DataBroker(allNet, log) {
    override val mapper = BasicMapper()
    override val url by lazy { "${allNet.gameUrl.ensureEndingSlash()}Maimai2Servlet" }

    override fun pull(): String {
        log("Game URL: ${allNet.gameUrl}")
        log("User ID: ${allNet.userId}")

        val userId = mapOf("userId" to allNet.userId)
        val paged = userId + mapOf("nextIndex" to 0, "maxCount" to 10000000)

        return Mai2UserAll().apply {
            userData = ls("GetUserDataApi".get("userData", userId))
//            userGameOption = ls("GetUserOptionApi".get("userGameOption", userId))

        }.toJson()
    }

}