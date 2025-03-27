package icu.samnyan.aqua.sega.ongeki.model

import ext.Bool

class CodeResp(
    var returnCode: Int = 0,
    var apiName: String? = null,
)

class GetUserPreviewResp(
    var userId: Long = 0,
    var isLogin: Bool = false,
    var lastLoginDate: String? = null,
    var userName: String = "",
    var reincarnationNum: Int = 0,
    var level: Int = 0,
    var exp: Long = 0,
    var playerRating: Long = 0,
    var lastGameId: String = "",
    var lastRomVersion: String = "",
    var lastDataVersion: String = "",
    var lastPlayDate: String? = null,
    var nameplateId: Int = 0,
    var trophyId: Int = 0,
    var cardId: Int = 0,
    var dispPlayerLv: Int = 0,
    var dispRating: Int = 0,
    var dispBP: Int = 0,
    var headphone: Int = 0,
    var banStatus: Int = 0,
    var isWarningConfirmed: Bool = false,
    var lastEmoneyBrand: Int = 0,
    var lastEmoneyCredit: Int = 0,
)

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
