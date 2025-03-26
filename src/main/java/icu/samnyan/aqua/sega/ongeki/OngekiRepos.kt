@file:Suppress("FunctionName")
package icu.samnyan.aqua.sega.ongeki

import icu.samnyan.aqua.sega.ongeki.model.gamedata.*
import icu.samnyan.aqua.net.games.GenericPlaylogRepo
import icu.samnyan.aqua.net.games.GenericUserDataRepo
import icu.samnyan.aqua.net.games.GenericUserMusicRepo
import icu.samnyan.aqua.sega.general.model.Card
import icu.samnyan.aqua.sega.ongeki.model.userdata.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository("OngekiUserActivityRepository")
interface UserActivityRepository : JpaRepository<UserActivity, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserActivity>

    fun findByUserAndKindAndActivityId(userData: UserData, kind: Int, activityId: Int): Optional<UserActivity>

    fun findByUser_Card_ExtIdAndKindOrderBySortNumberDesc(userId: Long, kind: Int): List<UserActivity>
}

@Repository("OngekiUserBossRepository")
interface UserBossRepository : JpaRepository<UserBoss, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserBoss>

    fun findByUserAndMusicId(user: UserData, musicId: Int): Optional<UserBoss>
}

@Repository("OngekiUserCardRepository")
interface UserCardRepository : JpaRepository<UserCard, Long> {
    fun findByUserAndCardId(userData: UserData, cardId: Int): Optional<UserCard>

    fun findByUser_Card_ExtId(userId: Long): List<UserCard>

    fun findByUser_Card_ExtId(userId: Long, page: Pageable): Page<UserCard>
}

@Repository("OngekiUserChapterRepository")
interface UserChapterRepository : JpaRepository<UserChapter, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserChapter>

    fun findByUserAndChapterId(userData: UserData, chapterId: Int): Optional<UserChapter>
}

@Repository("OngekiUserCharacterRepository")
interface UserCharacterRepository : JpaRepository<UserCharacter, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserCharacter>

    fun findByUser_Card_ExtId(userId: Long, page: Pageable): Page<UserCharacter>

    fun findByUserAndCharacterId(userData: UserData, characterId: Int): Optional<UserCharacter>
}

@Repository("OngekiUserDataRepository")
interface UserDataRepository : GenericUserDataRepo<UserData> {
    fun findByCard_ExtIdIn(userIds: Collection<Long>): List<UserData>
}

@Repository("OngekiUserDeckRepository")
interface UserDeckRepository : JpaRepository<UserDeck, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserDeck>

    fun findByUserAndDeckId(userData: UserData, deckId: Int): Optional<UserDeck>
}

@Repository("OngekiUserEventMusicRepository")
interface UserEventMusicRepository : JpaRepository<UserEventMusic, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserEventMusic>

    fun findByUserAndEventIdAndTypeAndMusicId(
        userData: UserData,
        eventId: Int,
        type: Int,
        musicId: Int
    ): Optional<UserEventMusic>
}

@Repository("OngekiUserEventPointRepository")
interface UserEventPointRepository : JpaRepository<UserEventPoint, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserEventPoint>

    fun findByUserAndEventId(userData: UserData, eventId: Int): Optional<UserEventPoint>

    //@Query(value = "SELECT rank from (SELECT user_id , DENSE_RANK() OVER (ORDER BY point DESC) as rank from ongeki_user_event_point where event_id = :eventId) where user_id == :userId limit 1", nativeQuery = true)
    @Query("SELECT COUNT(u)+1 FROM OngekiUserEventPoint u WHERE u.eventId = :eventId AND u.point > (SELECT u2.point FROM OngekiUserEventPoint u2 WHERE u2.user.id = :userId AND u2.eventId = :eventId)")
    fun calculateRankByUserAndEventId(userId: Long, eventId: Int): Int
}

@Repository("OngekiUserGeneralDataRepository")
interface UserGeneralDataRepository : JpaRepository<UserGeneralData, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserGeneralData>

    fun findByUserAndPropertyKey(user: UserData, key: String): Optional<UserGeneralData>

    fun findByUser_Card_ExtIdAndPropertyKey(userId: Long, key: String): Optional<UserGeneralData>
}

@Repository("OngekiUserItemRepository")
interface UserItemRepository : JpaRepository<UserItem, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserItem>

    fun findByUser_Card_ExtId(userId: Long, page: Pageable): Page<UserItem>

    fun findByUserAndItemKindAndItemId(userData: UserData, itemKind: Int, itemId: Int): Optional<UserItem>

    fun findByUser_Card_ExtIdAndItemKind(userId: Long, kind: Int, page: Pageable): Page<UserItem>
}

@Repository("OngekiUserKopRepository")
interface UserKopRepository : JpaRepository<UserKop, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserKop>

    fun findByUserAndKopIdAndAreaId(userData: UserData, kopId: Int, areaId: Int): Optional<UserKop>
}

interface UserLoginBonusRepository : JpaRepository<UserLoginBonus, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserLoginBonus>

    fun findByUserAndBonusId(userData: UserData, bonusId: Int): Optional<UserLoginBonus>
}

@Repository("OngekiUserMemoryChapterRepository")
interface UserMemoryChapterRepository : JpaRepository<UserMemoryChapter, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserMemoryChapter>

    fun findByUserAndChapterId(userData: UserData, chapterId: Int): Optional<UserMemoryChapter>
}

@Repository("OngekiUserMissionPointRepository")
interface UserMissionPointRepository : JpaRepository<UserMissionPoint, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserMissionPoint>

    fun findByUserAndEventId(userData: UserData, eventId: Int): Optional<UserMissionPoint>
}

@Repository("OngekiUserMusicDetailRepository")
interface UserMusicDetailRepository : JpaRepository<UserMusicDetail, Long>,
    GenericUserMusicRepo<UserMusicDetail> {
    fun findByUser_Card_ExtId(userId: Long): List<UserMusicDetail>

    fun findByUser_Card_ExtId(userId: Long, page: Pageable): Page<UserMusicDetail>

    fun findByUserAndMusicIdAndLevel(userData: UserData, musicId: Int, level: Int): Optional<UserMusicDetail>
}

@Repository("OngekiUserMusicItemRepository")
interface UserMusicItemRepository : JpaRepository<UserMusicItem, Long> {
    fun findByUser_Card_ExtId(aimeId: Long): List<UserMusicItem>

    fun findByUser_Card_ExtId(userId: Long, page: Pageable): Page<UserMusicItem>

    fun findByUserAndMusicId(userData: UserData, musicId: Int): Optional<UserMusicItem>
}

@Repository("OngekiUserOptionRepository")
interface UserOptionRepository : JpaRepository<UserOption, Long> {
    fun findByUser(userData: UserData): Optional<UserOption>

    fun findByUser_Card_ExtId(userId: Long): Optional<UserOption>
}

@Repository("OngekiUserPlaylogRepository")
interface UserPlaylogRepository : GenericPlaylogRepo<UserPlaylog> {
    fun findByUser_Card_ExtId(userId: Long): List<UserPlaylog>

    fun findByUser_Card_ExtId(userId: Long, page: Pageable): Page<UserPlaylog>
}

@Repository("OngekiUserRivalDataRepository")
interface UserRivalDataRepository : JpaRepository<UserRival, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserRival>
}

@Repository("OngekiUserScenarioRepository")
interface UserScenarioRepository : JpaRepository<UserScenario, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserScenario>

    fun findByUserAndScenarioId(user: UserData, scenarioId: Int): Optional<UserScenario>
}

@Repository("OngekiUserStoryRepository")
interface UserStoryRepository : JpaRepository<UserStory, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserStory>

    fun findByUserAndStoryId(userData: UserData, storyId: Int): Optional<UserStory>
}

@Repository("OngekiUserTechCountRepository")
interface UserTechCountRepository : JpaRepository<UserTechCount, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserTechCount>

    fun findByUserAndLevelId(user: UserData, levelId: Int): Optional<UserTechCount>
}

@Repository("OngekiUserTechEventRepository")
interface UserTechEventRepository : JpaRepository<UserTechEvent, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserTechEvent>

    fun findByUserAndEventId(userData: UserData, eventId: Int): Optional<UserTechEvent>
}

@Repository("OngekiUserTradeItemRepository")
interface UserTradeItemRepository : JpaRepository<UserTradeItem, Long> {
    fun findByUser_Card_ExtId(userId: Long): List<UserTradeItem>

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
interface UserTrainingRoomRepository : JpaRepository<UserTrainingRoom, Long> {
    fun findByUserAndRoomId(user: UserData, roomId: Int): Optional<UserTrainingRoom>

    fun findByUser_Card_ExtId(userId: Long): List<UserTrainingRoom>
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
