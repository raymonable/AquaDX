package icu.samnyan.aqua.sega.ongeki.model.userdata

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import icu.samnyan.aqua.net.games.IGenericGamePlaylog
import icu.samnyan.aqua.net.games.IGenericUserMusic
import icu.samnyan.aqua.net.games.IUserData
import icu.samnyan.aqua.sega.general.model.Card
import icu.samnyan.aqua.sega.util.jackson.AccessCodeSerializer
import jakarta.persistence.*

@Entity(name = "OngekiUserActivity")
@Table(
    name = "ongeki_user_activity",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "kind", "activity_id"])]
)
@JsonPropertyOrder("kind", "id", "sortNumber", "param1", "param2", "param3", "param4")
class UserActivity(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var kind = 0
    @JsonProperty("id")
    @Column(name = "activity_id")
    var activityId = 0
    var sortNumber = 0
    var param1 = 0
    var param2 = 0
    var param3 = 0
    var param4 = 0
}


@Entity(name = "OngekiUserBoss")
@Table(name = "ongeki_user_boss")
class UserBoss(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var musicId = 0
    var damage = 0
    @JsonProperty("isClear")
    var isClear = false
    var eventId = 0
}


@Entity(name = "OngekiUserCard")
@Table(name = "ongeki_user_card")
class UserCard  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserData = UserData()
    var cardId = -1

    var digitalStock = 1
    var analogStock = 0
    var level = 0
    var maxLevel = 10
    var exp = 0
    var printCount = 0
    var useCount = 0
    @JsonProperty("isNew")
    var isNew = true
    var kaikaDate = "0000-00-00 00:00:00.0"

    var choKaikaDate = "0000-00-00 00:00:00.0"

    var skillId = 0
    @JsonProperty("isAcquired")
    var isAcquired = true
    var created = "0000-00-00 00:00:00.0"

    constructor(userData: UserData) {
        this.user = userData
    }

    constructor(userData: UserData, cardId: Int, skillId: Int, created: String) {
        this.user = userData
        this.cardId = cardId
        this.skillId = skillId
        this.created = created
    }

}


@Entity(name = "OngekiUserChapter")
@Table(name = "ongeki_user_chapter")

class UserChapter(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var chapterId = 0
    var jewelCount = 0
    var lastPlayMusicCategory = 0
    var lastPlayMusicId = 0
    var lastPlayMusicLevel = 0
    @JsonProperty("isStoryWatched")
    var isStoryWatched = false
    @JsonProperty("isClear")
    var isClear = false
    var skipTiming1 = 0
    var skipTiming2 = 0
}


@Entity(name = "OngekiUserCharacter")
@Table(name = "ongeki_user_character")

class UserCharacter(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var characterId = 0
    var costumeId = 0
    var attachmentId = 0
    var playCount = 0
    var intimateLevel = 0
    var intimateCount = 0
    var intimateCountRewarded = 0
    var intimateCountDate: String = ""
    @JsonProperty("isNew")
    var isNew = false
}


@Entity(name = "OngekiUserData")
@Table(name = "ongeki_user_data")
class UserData : IUserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    override var id: Long = 0
    @JsonSerialize(using = AccessCodeSerializer::class)
    @JsonProperty(value = "accessCode", access = JsonProperty.Access.READ_ONLY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aime_card_id", unique = true)
    override var card: Card? = null
    // Access code in card
    override var userName: String = ""

    var level = 0
    var reincarnationNum = 0
    var exp: Long = 0
    var point: Long = 0
    var totalPoint: Long = 0
    var playCount = 0
    var jewelCount = 0
    var totalJewelCount = 0
    var medalCount = 0
    override var playerRating: Int = 0
    override var highestRating: Int = 0
    var battlePoint = 0
    var bestBattlePoint = 0
    var overDamageBattlePoint = 0
    @JsonProperty("isDialogWatchedSuggestMemory")
    var isDialogWatchedSuggestMemory = false
    var nameplateId = 0
    var trophyId = 0
    var cardId = 0
    var characterId = 0
    var characterVoiceNo = 0
    var tabSetting = 0
    var tabSortSetting = 0
    var cardCategorySetting = 0
    var cardSortSetting = 0
    var rivalScoreCategorySetting = 0
    var playedTutorialBit = 0
    var firstTutorialCancelNum = 0
    var sumTechHighScore: Long = 0
    var sumTechBasicHighScore: Long = 0
    var sumTechAdvancedHighScore: Long = 0
    var sumTechExpertHighScore: Long = 0
    var sumTechMasterHighScore: Long = 0
    var sumTechLunaticHighScore: Long = 0
    var sumBattleHighScore: Long = 0
    var sumBattleBasicHighScore: Long = 0
    var sumBattleAdvancedHighScore: Long = 0
    var sumBattleExpertHighScore: Long = 0
    var sumBattleMasterHighScore: Long = 0
    var sumBattleLunaticHighScore: Long = 0
    var eventWatchedDate: String = ""
    var cmEventWatchedDate: String = ""
    var firstGameId: String = ""
    var firstRomVersion: String = ""
    var firstDataVersion: String = ""
    override var firstPlayDate: String = ""
    var lastGameId: String = ""
    override var lastRomVersion: String = ""
    var lastDataVersion: String = ""
    var compatibleCmVersion: String = ""
    override var lastPlayDate: String = ""
    var lastPlaceId = 0
    var lastPlaceName: String = ""
    var lastRegionId = 0
    var lastRegionName: String = ""
    var lastAllNetId = 0
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override var lastClientId: String = ""
    var lastUsedDeckId = 0
    var lastPlayMusicLevel = 0
    var lastEmoneyBrand = 0

    override val totalScore get() = sumTechHighScore
}


@Entity(name = "OngekiUserDeck")
@Table(name = "ongeki_user_deck")

class UserDeck(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var deckId = 0
    var cardId1 = 0
    var cardId2 = 0
    var cardId3 = 0
}


@Entity(name = "OngekiUserEventMusic")
@Table(name = "ongeki_user_event_music")

class UserEventMusic(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var eventId = 0
    var type = 0
    var musicId = 0
    var level = 0
    var techScoreMax = 0
    var platinumScoreMax = 0
    var techRecordDate: String = ""
    @JsonProperty("isTechNewRecord")
    var isTechNewRecord: Boolean = false
}


/**
 * For chapter event.
 * @author samnyan (privateamusement@protonmail.com)
 */
@Entity(name = "OngekiUserEventPoint")
@Table(name = "ongeki_user_event_point")

class UserEventPoint(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var eventId = 0
    var point: Long = 0
    @JsonProperty("isRankingRewarded")
    var isRankingRewarded = false
}


/**
 * This is for storing some data only use in aqua
 * @author samnyan (privateamusement@protonmail.com)
 */
@Entity(name = "OngekiUserGeneralData")
@Table(name = "ongeki_user_general_data")

class UserGeneralData(
    @field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData,
    var propertyKey: String
)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    @Column(columnDefinition = "TEXT")
    var propertyValue = ""

}


@Entity(name = "OngekiUserItem")
@Table(name = "ongeki_user_item")

class UserItem(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var itemKind = 0
    var itemId = 0
    var stock = 0
    @JsonProperty("isValid")
    var isValid = false
}


@Entity(name = "OngekiUserKop")
@Table(name = "ongeki_user_kop")

class UserKop(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var authKey: String = ""
    var kopId = 0
    var areaId = 0
    var totalTechScore = 0
    var totalPlatinumScore = 0
    var techRecordDate: String = ""
    @JsonProperty("isTotalTechNewRecord")
    var isTotalTechNewRecord = false
}


@Entity(name = "OngekiUserLoginBonus")
@Table(name = "ongeki_user_login_bonus")

class UserLoginBonus(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var bonusId = 0
    var bonusCount = 0
    var lastUpdateDate: String = ""
}


@Entity(name = "OngekiUserMemoryChapter")
@Table(name = "ongeki_user_memory_chapter")

class UserMemoryChapter(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var chapterId = 0
    var jewelCount = 0
    var lastPlayMusicCategory = 0
    var lastPlayMusicId = 0
    var lastPlayMusicLevel = 0
    @JsonProperty("isDialogWatched")
    var isDialogWatched = false
    @JsonProperty("isStoryWatched")
    var isStoryWatched = false
    @JsonProperty("isBossWatched")
    var isBossWatched = false
    @JsonProperty("isClear")
    var isClear = false
    var gaugeId = 0
    var gaugeNum = 0
}

@Entity(name = "OngekiUserMissionPoint")
@Table(name = "ongeki_user_mission_point")
class UserMissionPoint(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var eventId = 0
    var point: Long = 0
}


@Entity(name = "OngekiUserMusicDetail")
@Table(name = "ongeki_user_music_detail")
class UserMusicDetail(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData) :
    IGenericUserMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    override var musicId: Int = 0
    var level = 0
    var playCount = 0
    var techScoreMax = 0
    var techScoreRank = 0
    var battleScoreMax = 0
    var battleScoreRank = 0
    var platinumScoreMax = 0
    var maxComboCount = 0
    var maxOverKill = 0
    var maxTeamOverKill = 0
    @JsonProperty("isFullBell")
    var isFullBell = false
    @JsonProperty("isFullCombo")
    var isFullCombo = false
    @JsonProperty("isAllBreake")
    var isAllBreake = false
    @JsonProperty("isLock")
    var isLock = false
    var clearStatus = 0
    var isStoryWatched = false
}


@Entity(name = "OngekiUserMusicItem")
@Table(name = "ongeki_user_music_item")

class UserMusicItem(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var musicId = 0
    var status = 0
}


@Entity(name = "OngekiUserOption")
@Table(name = "ongeki_user_option")

class UserOption(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var optionSet = 0
    var speed = 0
    var mirror = 0
    var judgeTiming = 0
    var judgeAdjustment = 0
    var abort = 0
    var stealthField = 0
    var tapSound = 0
    var volGuide = 0
    var volAll = 0
    var volTap = 0
    var volCrTap = 0
    var volHold = 0
    var volSide = 0
    var volFlick = 0
    var volBell = 0
    var volEnemy = 0
    var volSkill = 0
    var volDamage = 0
    var colorField = 0
    var colorLaneBright = 0
    var colorWallBright = 0
    var colorLane = 0
    var colorSide = 0
    var effectDamage = 0
    var effectPos = 0
    var judgeDisp = 0
    var judgePos = 0
    var judgeBreak = 0
    var judgeHit = 0
    var platinumBreakDisp = 0
    var judgeCriticalBreak = 0
    var matching = 0
    var dispPlayerLv = 0
    var dispRating = 0
    var dispBP = 0
    var headphone = 0
}


@Entity(name = "OngekiUserPlaylog")
@Table(name = "ongeki_user_playlog")
class UserPlaylog : IGenericGamePlaylog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    override var user: UserData = UserData()
    var sortNumber = 0
    var placeId = 0
    var placeName: String = ""
    var playDate: String = ""
    override var userPlayDate: String = ""
    override var musicId: Int = 0
    override var level: Int = 0
    var playKind = 0
    var eventId = 0
    var eventName: String = ""
    var eventPoint = 0
    var playedUserId1 = 0
    var playedUserId2 = 0
    var playedUserId3 = 0
    var playedUserName1: String = ""
    var playedUserName2: String = ""
    var playedUserName3: String = ""
    var playedMusicLevel1 = 0
    var playedMusicLevel2 = 0
    var playedMusicLevel3 = 0
    var cardId1 = 0
    var cardId2 = 0
    var cardId3 = 0
    var cardLevel1 = 0
    var cardLevel2 = 0
    var cardLevel3 = 0
    var cardAttack1 = 0
    var cardAttack2 = 0
    var cardAttack3 = 0
    var bossCharaId = 0
    var bossLevel = 0
    var bossAttribute = 0
    var clearStatus = 0
    var techScore = 0
    var techScoreRank = 0
    var battleScore = 0
    var battleScoreRank = 0
    var platinumScore = 0
    override var maxCombo: Int = 0
    var judgeMiss = 0
    var judgeHit = 0
    var judgeBreak = 0
    var judgeCriticalBreak = 0
    var rateTap = 0
    var rateHold = 0
    var rateFlick = 0
    var rateSideTap = 0
    var rateSideHold = 0
    var bellCount = 0
    var totalBellCount = 0
    var damageCount = 0
    var overDamage = 0
    @JsonProperty("isTechNewRecord")
    var isTechNewRecord = false
    @JsonProperty("isBattleNewRecord")
    var isBattleNewRecord = false
    @JsonProperty("isOverDamageNewRecord")
    var isOverDamageNewRecord = false
    @JsonProperty("isFullCombo")
    override var isFullCombo: Boolean = false
    @JsonProperty("isFullBell")
    var isFullBell = false
    @JsonProperty("isAllBreak")
    var isAllBreak: Boolean = false
    var playerRating: Int = 0

    var battlePoint = 0

    override val isAllPerfect get() = isAllBreak
    override val beforeRating get() = playerRating
    override val afterRating get() = playerRating
    override val achievement get() = techScore
}


@Entity(name = "OngekiUserRival")
@Table(name = "ongeki_user_rival")
class UserRival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    var user: UserData? = null

    @JoinColumn(name = "rival_user_ext_id")
    @JsonProperty("rivalUserId")
    var rivalUserExtId: Long = 0
}

@Entity(name = "OngekiUserScenario")
@Table(name = "ongeki_user_scenario")
class UserScenario(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var scenarioId = 0
    var playCount = 0
}


@Entity(name = "OngekiUserStory")
@Table(name = "ongeki_user_story")
class UserStory(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var storyId = 0
    var lastChapterId = 0
    var jewelCount = 0
    var lastPlayMusicId = 0
    var lastPlayMusicCategory = 0
    var lastPlayMusicLevel = 0
}


@Entity(name = "OngekiUserTechCount")
@Table(name = "ongeki_user_tech_count")
class UserTechCount(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var levelId = 0
    var allBreakCount = 0
    var allBreakPlusCount = 0
}


@Entity(name = "OngekiUserTechEvent")
@Table(name = "ongeki_user_tech_event")
class UserTechEvent(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var eventId = 0
    var totalTechScore = 0
    var totalPlatinumScore = 0
    var techRecordDate: String = ""
    @JsonProperty("isRankingRewarded")
    var isRankingRewarded = false
    @JsonProperty("isTotalTechNewRecord")
    var isTotalTechNewRecord = false
}


@Entity(name = "OngekiUserTradeItem")
@Table(name = "ongeki_user_trade_item")

class UserTradeItem(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var chapterId = 0
    var tradeItemId = 0
    var tradeCount = 0
}


@Entity(name = "OngekiTrainingRoom")
@Table(name = "ongeki_user_training_room")

class UserTrainingRoom(@field:JoinColumn(name = "user_id") @field:ManyToOne @field:JsonIgnore var user: UserData)  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    var authKey: String = ""
    var roomId: Int = 0
    var cardId: Int = 0
    var valueDate: String = ""
}
