package icu.samnyan.aqua.sega.ongeki.model

class CodeResp(
    var returnCode: Int = 0,
    var apiName: String? = null,
)

class OngekiFumenScore(
    var musicId: Int = 0,
    var difficultId: Int = 0,
    var romVersionCode: String? = null,
    var score: Int = 0,
    var platinumScoreMax: Int = 0,
    var platinumScoreStar: Int = 0,
) {
    override fun toString() = "${musicId}:${difficultId}:${score}:${platinumScoreStar}:${platinumScoreMax}"
}

class GameEventItem(
    var id: Long = 0,
    var type: Int = 0,
    var startDate: String = "",
    var endDate: String = "",
)
