package icu.samnyan.aqua.sega.ongeki.model.response.data

import ext.Bool
import icu.samnyan.aqua.sega.ongeki.model.UserMusicDetail

class GameEventItem(
    var id: Long = 0,
    var type: Int = 0,
    var startDate: String = "",
    var endDate: String = "",
)

class GameIdListItem(
    var id: Int = 0,
    var type: Int = 0,
)

class GameRankingItem(
    var id: Long = 0,

    // this 2 field never use in game code,
    // maybe for the future update like in game player ranking
    var point: Long = 0,
    var userName: String = "",
)

class GameSetting(
    var dataVersion: String = "",
    var onlineDataVersion: String = "",
    var isMaintenance: Bool = false,
    var requestInterval: Int = 0,
    var rebootStartTime: String = "",
    var rebootEndTime: String = "",
    var isBackgroundDistribute: Bool = false,
    var maxCountCharacter: Int = 0,
    var maxCountCard: Int = 0,
    var maxCountItem: Int = 0,
    var maxCountMusic: Int = 0,
    var maxCountMusicItem: Int = 0,
    var maxCountRivalMusic: Int = 0,
)

class UserEventRankingItem(
    var eventId: Int = 0,
    var type: Int = 0,
    var date: String = "",
    var rank: Int = 0,
    var point: Long = 0,
)

class UserMusicListItem(
    var length: Int = 0,
    var userMusicDetailList: List<UserMusicDetail>? = null,
)

class UserRivalData(
    var rivalUserId: Long = 0,
    var rivalUserName: String = "",
)

class UserRivalMusic(
    var userRivalMusicDetailList: List<UserMusicDetail>? = null,
    var length: Int = 0,
)

class UserTechEventRankingItem(
    var eventId: Int = 0,
    var date: String = "",
    var rank: Int = 0,
    var totalTechScore: Int = 0,
    var totalPlatinumScore: Int = 0,
)
