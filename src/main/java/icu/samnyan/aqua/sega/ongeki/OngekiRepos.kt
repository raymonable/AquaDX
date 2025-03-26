@file:Suppress("FunctionName")
package icu.samnyan.aqua.sega.ongeki

import icu.samnyan.aqua.net.games.GenericPlaylogRepo
import icu.samnyan.aqua.net.games.GenericUserDataRepo
import icu.samnyan.aqua.net.games.GenericUserMusicRepo
import icu.samnyan.aqua.net.games.IUserRepo
import icu.samnyan.aqua.sega.ongeki.model.gamedata.*
import icu.samnyan.aqua.sega.ongeki.model.userdata.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository
import java.util.*


@NoRepositoryBean
interface OngekiUserLinked<T> : IUserRepo<UserData, T> {
    fun findByUser_Card_ExtId(extId: Long): List<T>
    fun findSingleByUser_Card_ExtId(extId: Long): Optional<T>
    fun findByUser_Card_ExtId(extId: Long, pageable: Pageable): Page<T>
}

@Repository("OngekiUserDataRepository")
interface UserDataRepository : GenericUserDataRepo<UserData> {
    fun findByCard_ExtIdIn(userIds: Collection<Long>): List<UserData>
}

@Repository("OngekiUserActivityRepository")
interface UserActivityRepository : OngekiUserLinked<UserActivity> {
    fun findByUserAndKindAndActivityId(userData: UserData, kind: Int, activityId: Int): Optional<UserActivity>
    fun findByUser_Card_ExtIdAndKindOrderBySortNumberDesc(userId: Long, kind: Int): List<UserActivity>
}

@Repository("OngekiUserBossRepository")
interface UserBossRepository : OngekiUserLinked<UserBoss> {
    fun findByUserAndMusicId(user: UserData, musicId: Int): Optional<UserBoss>
}

@Repository("OngekiUserCardRepository")
interface UserCardRepository : OngekiUserLinked<UserCard> {
    fun findByUserAndCardId(userData: UserData, cardId: Int): Optional<UserCard>
}

@Repository("OngekiUserChapterRepository")
interface UserChapterRepository : OngekiUserLinked<UserChapter> {
    fun findByUserAndChapterId(userData: UserData, chapterId: Int): Optional<UserChapter>
}

@Repository("OngekiUserCharacterRepository")
interface UserCharacterRepository : OngekiUserLinked<UserCharacter> {
    fun findByUserAndCharacterId(userData: UserData, characterId: Int): Optional<UserCharacter>
}

@Repository("OngekiUserDeckRepository")
interface UserDeckRepository : OngekiUserLinked<UserDeck> {
    fun findByUserAndDeckId(userData: UserData, deckId: Int): Optional<UserDeck>
}

@Repository("OngekiUserEventMusicRepository")
interface UserEventMusicRepository : OngekiUserLinked<UserEventMusic> {
    fun findByUserAndEventIdAndTypeAndMusicId(
        userData: UserData,
        eventId: Int,
        type: Int,
        musicId: Int
    ): Optional<UserEventMusic>
}

@Repository("OngekiUserEventPointRepository")
interface UserEventPointRepository : OngekiUserLinked<UserEventPoint> {
    fun findByUserAndEventId(userData: UserData, eventId: Int): Optional<UserEventPoint>

    //@Query(value = "SELECT rank from (SELECT user_id , DENSE_RANK() OVER (ORDER BY point DESC) as rank from ongeki_user_event_point where event_id = :eventId) where user_id == :userId limit 1", nativeQuery = true)
    @Query("SELECT COUNT(u)+1 FROM OngekiUserEventPoint u WHERE u.eventId = :eventId AND u.point > (SELECT u2.point FROM OngekiUserEventPoint u2 WHERE u2.user.id = :userId AND u2.eventId = :eventId)")
    fun calculateRankByUserAndEventId(userId: Long, eventId: Int): Int
}

@Repository("OngekiUserGeneralDataRepository")
interface UserGeneralDataRepository : OngekiUserLinked<UserGeneralData> {
    fun findByUserAndPropertyKey(user: UserData, key: String): Optional<UserGeneralData>

    fun findByUser_Card_ExtIdAndPropertyKey(userId: Long, key: String): Optional<UserGeneralData>
}

@Repository("OngekiUserItemRepository")
interface UserItemRepository : OngekiUserLinked<UserItem> {
    fun findByUserAndItemKindAndItemId(userData: UserData, itemKind: Int, itemId: Int): Optional<UserItem>

    fun findByUser_Card_ExtIdAndItemKind(userId: Long, kind: Int, page: Pageable): Page<UserItem>
}

@Repository("OngekiUserKopRepository")
interface UserKopRepository : OngekiUserLinked<UserKop> {
    fun findByUserAndKopIdAndAreaId(userData: UserData, kopId: Int, areaId: Int): Optional<UserKop>
}

interface UserLoginBonusRepository : OngekiUserLinked<UserLoginBonus> {
    fun findByUserAndBonusId(userData: UserData, bonusId: Int): Optional<UserLoginBonus>
}

@Repository("OngekiUserMemoryChapterRepository")
interface UserMemoryChapterRepository : OngekiUserLinked<UserMemoryChapter> {
    fun findByUserAndChapterId(userData: UserData, chapterId: Int): Optional<UserMemoryChapter>
}

@Repository("OngekiUserMissionPointRepository")
interface UserMissionPointRepository : OngekiUserLinked<UserMissionPoint> {
    fun findByUserAndEventId(userData: UserData, eventId: Int): Optional<UserMissionPoint>
}

@Repository("OngekiUserMusicDetailRepository")
interface UserMusicDetailRepository : OngekiUserLinked<UserMusicDetail>, GenericUserMusicRepo<UserMusicDetail> {
    fun findByUserAndMusicIdAndLevel(userData: UserData, musicId: Int, level: Int): Optional<UserMusicDetail>
}

@Repository("OngekiUserMusicItemRepository")
interface UserMusicItemRepository : OngekiUserLinked<UserMusicItem> {
    fun findByUserAndMusicId(userData: UserData, musicId: Int): Optional<UserMusicItem>
}

@Repository("OngekiUserOptionRepository")
interface UserOptionRepository : OngekiUserLinked<UserOption>

@Repository("OngekiUserPlaylogRepository")
interface UserPlaylogRepository : OngekiUserLinked<UserPlaylog>, GenericPlaylogRepo<UserPlaylog>

@Repository("OngekiUserRivalDataRepository")
interface UserRivalDataRepository : OngekiUserLinked<UserRival>

@Repository("OngekiUserScenarioRepository")
interface UserScenarioRepository : OngekiUserLinked<UserScenario> {
    fun findByUserAndScenarioId(user: UserData, scenarioId: Int): Optional<UserScenario>
}

@Repository("OngekiUserStoryRepository")
interface UserStoryRepository : OngekiUserLinked<UserStory> {
    fun findByUserAndStoryId(userData: UserData, storyId: Int): Optional<UserStory>
}

@Repository("OngekiUserTechCountRepository")
interface UserTechCountRepository : OngekiUserLinked<UserTechCount> {
    fun findByUserAndLevelId(user: UserData, levelId: Int): Optional<UserTechCount>
}

@Repository("OngekiUserTechEventRepository")
interface UserTechEventRepository : OngekiUserLinked<UserTechEvent> {
    fun findByUserAndEventId(userData: UserData, eventId: Int): Optional<UserTechEvent>
}

@Repository("OngekiUserTradeItemRepository")
interface UserTradeItemRepository : OngekiUserLinked<UserTradeItem> {
    fun findByUser_Card_ExtIdAndChapterIdGreaterThanEqualAndChapterIdLessThanEqual(
        userId: Long,
        startChapterId: Int,
        endChapterId: Int
    ): List<UserTradeItem>

    fun findByUserAndChapterIdAndTradeItemId(
        userData: UserData,
        chapterId: Int,
        tradeItemId: Int
    ): Optional<UserTradeItem>
}

@Repository("OngekiUserTrainingRoomRepository")
interface UserTrainingRoomRepository : OngekiUserLinked<UserTrainingRoom> {
    fun findByUserAndRoomId(user: UserData, roomId: Int): Optional<UserTrainingRoom>
}


@Repository("OngekiGameCardRepository")
interface GameCardRepository : JpaRepository<GameCard, Long>

@Repository("OngekiGameCharaRepository")
interface GameCharaRepository : JpaRepository<GameChara, Long>

@Repository("OngekiGameEventRepository")
interface GameEventRepository : JpaRepository<GameEvent, Long>

@Repository("OngekiGameMusicRepository")
interface GameMusicRepository : JpaRepository<GameMusic, Long>

@Repository("OngekiGamePointRepository")
interface GamePointRepository : JpaRepository<GamePoint, Long>

@Repository("OngekiGamePresentRepository")
interface GamePresentRepository : JpaRepository<GamePresent, Long>

@Repository("OngekiGameRewardRepository")
interface GameRewardRepository : JpaRepository<GameReward, Long>

@Repository("OngekiGameSkillRepository")
interface GameSkillRepository : JpaRepository<GameSkill, Long>
