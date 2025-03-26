package icu.samnyan.aqua.sega.ongeki.model.response.data

import icu.samnyan.aqua.sega.ongeki.model.UserMusicDetail
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
class GameEventItem {
    var id: Long = 0
    var type = 0
    var startDate: String = ""
    var endDate: String = ""
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GameIdListItem {
    var id = 0
    var type = 0
}

/**
 * Fro getGameRanking request
 * @author samnyan (privateamusement@protonmail.com)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class GameRankingItem {
    var id: Long = 0

    // this 2 field never use in game code,
    // maybe for the future update like in game player ranking
    var point: Long = 0
    var userName: String = ""
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GameSetting {
    var dataVersion: String = ""
    var onlineDataVersion: String = ""
    var isMaintenance = false
    var requestInterval = 0
    var rebootStartTime: String = ""
    var rebootEndTime: String = ""
    var isBackgroundDistribute = false
    var maxCountCharacter = 0
    var maxCountCard = 0
    var maxCountItem = 0
    var maxCountMusic = 0
    var maxCountMusicItem = 0
    var maxCountRivalMusic = 0
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserEventRankingItem {
    var eventId = 0
    var type = 0
    var date: String = ""
    var rank = 0
    var point: Long = 0
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserMusicListItem {
    var length = 0
    var userMusicDetailList: List<UserMusicDetail>? = null
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserRivalData {
    var rivalUserId: Long = 0
    var rivalUserName: String = ""
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserRivalMusic {
    var userRivalMusicDetailList: List<UserMusicDetail>? = null
    var length = 0
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserTechEventRankingItem {
    var eventId = 0
    var date: String = ""
    var rank = 0
    var totalTechScore = 0
    var totalPlatinumScore = 0
}
