package icu.samnyan.aqua.sega.ongeki.handler.impl

import ext.logger
import icu.samnyan.aqua.sega.general.BaseHandler
import icu.samnyan.aqua.sega.general.model.response.UserRecentRating
import icu.samnyan.aqua.sega.general.service.CardService
import icu.samnyan.aqua.sega.ongeki.*
import icu.samnyan.aqua.sega.ongeki.model.request.UpsertUserAll
import icu.samnyan.aqua.sega.ongeki.model.response.CodeResp
import icu.samnyan.aqua.sega.ongeki.model.userdata.*
import icu.samnyan.aqua.sega.util.jackson.BasicMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * The handler for saving all data of a ONGEKI profile
 * @author samnyan (privateamusement@protonmail.com)
 */
@Component("OngekiUserAllHandler")
class UpsertUserAllHandler @Autowired constructor(
    private val mapper: BasicMapper,
    private val cardService: CardService,
    private val userDataRepository: OgkUserDataRepo,
    private val userOptionRepository: OgkUserOptionRepo,
    private val userPlaylogRepository: OgkUserPlaylogRepo,
    private val userActivityRepository: OgkUserActivityRepo,
    private val userMusicDetailRepository: OgkUserMusicDetailRepo,
    private val userCharacterRepository: OgkUserCharacterRepo,
    private val userCardRepository: OgkUserCardRepo,
    private val userDeckRepository: OgkUserDeckRepo,
    private val userStoryRepository: OgkUserStoryRepo,
    private val userChapterRepository: OgkUserChapterRepo,
    private val userItemRepository: OgkUserItemRepo,
    private val userMusicItemRepository: OgkUserMusicItemRepo,
    private val userLoginBonusRepository: OgkUserLoginBonusRepo,
    private val userEventPointRepository: OgkUserEventPointRepo,
    private val userMissionPointRepository: OgkUserMissionPointRepo,
    private val userTrainingRoomRepository: OgkUserTrainingRoomRepo,
    private val userGeneralDataRepository: OgkUserGeneralDataRepo,
    private val userBossRepository: OgkUserBossRepo,
    private val userScenarioRepository: OgkUserScenarioRepo,
    private val userTechCountRepository: OgkUserTechCountRepo,
    private val userTradeItemRepository: OgkUserTradeItemRepo,
    private val userEventMusicRepository: OgkUserEventMusicRepo,
    private val userTechEventRepository: OgkUserTechEventRepo,
    private val userKopRepository: OgkUserKopRepo,
    private val userMemoryChapterRepository: OgkUserMemoryChapterRepo
) : BaseHandler {
    override fun handle(request: Map<String, Any>): Any? {
        val userId = (request["userId"] as Number).toLong()
        val upsertUserAll = mapper.convert(
            request["upsertUserAll"]!!,
            UpsertUserAll::class.java
        )

        // All the field should exist, no need to check now.
        // UserData
        val u: UserData
        run {
            val userData: UserData
            val userOptional = userDataRepository.findByCard_ExtId(userId)

            // UserData might be empty on later runs
            if (userOptional.isEmpty && upsertUserAll.userData.isEmpty()) {
                return null
            }

            if (userOptional.isPresent) {
                userData = userOptional.get()
            } else {
                userData = UserData()
                val card = cardService.getCardByExtId(userId).orElseThrow()
                userData.card = card
            }

            // If new data exists, use new data. Otherwise, use old data
            u = if (!upsertUserAll.userData.isEmpty()) upsertUserAll.userData[0] else userData

            u.id = userData.id
            u.card = userData.card

            // Set eventWatchedDate with lastPlayDate, because client doesn't update it
            u.eventWatchedDate = userData.lastPlayDate
            u.cmEventWatchedDate = userData.lastPlayDate
            userDataRepository.save<UserData>(u)
        }


        // UserOption
        val newUserOption = upsertUserAll.userOption[0]

        val userOptionOptional = userOptionRepository.findSingleByUser(u)
        val userOption = userOptionOptional.orElseGet { UserOption().apply { user = u } }

        newUserOption.id = userOption.id
        newUserOption.user = userOption.user

        userOptionRepository.save(newUserOption)


        // UserPlaylogList
        val userPlaylogList = upsertUserAll.userPlaylogList
        val newUserPlaylogList: MutableList<UserPlaylog> = ArrayList()

        for (newUserPlaylog in userPlaylogList) {
            newUserPlaylog.user = u
            newUserPlaylogList.add(newUserPlaylog)
        }

        userPlaylogRepository.saveAll(newUserPlaylogList)


        // UserSessionlogList, UserJewelboostlogLost doesn't need to be saved for a private server


        // UserActivityList
        val userActivityList = upsertUserAll.userActivityList
        val newUserActivityList: MutableList<UserActivity> = ArrayList()

        for (newUserActivity in userActivityList) {
            val kind = newUserActivity.kind
            val id = newUserActivity.activityId

            if (kind != 0 && id != 0) {
                val activityOptional = userActivityRepository.findByUserAndKindAndActivityId(u, kind, id)
                val userActivity = activityOptional.orElseGet {
                    UserActivity().apply { user = u }
                }

                newUserActivity.id = userActivity.id
                newUserActivity.user = u
                newUserActivityList.add(newUserActivity)
            }
        }
        newUserActivityList.sortWith { a, b -> b.sortNumber.compareTo(a.sortNumber) }
        userActivityRepository.saveAll(newUserActivityList)


        // UserRecentRatingList
        // This thing still need to save to solve the rating drop
        this.saveGeneralData(upsertUserAll.userRecentRatingList, u, "recent_rating_list")


        /*
         * The rating and battle point calculation is little bit complex.
         * So I just create a UserGeneralData class to store this value
         * into a csv format for convenience
         */
        // UserBpBaseList (For calculating Battle point)
        this.saveGeneralData(upsertUserAll.userBpBaseList, u, "battle_point_base")


        // This is the best rating of all charts. Best 30 + 10 after that.
        // userRatingBaseBestList
        this.saveGeneralData(upsertUserAll.userRatingBaseBestList, u, "rating_base_best")


        // userRatingBaseNextList
        this.saveGeneralData(upsertUserAll.userRatingBaseNextList, u, "rating_base_next")


        // This is the best rating of new charts. Best 15 + 10 after that.
        // New chart means same version
        // userRatingBaseBestNewList
        this.saveGeneralData(upsertUserAll.userRatingBaseBestNewList, u, "rating_base_new_best")

        // userRatingBaseNextNewList
        this.saveGeneralData(upsertUserAll.userRatingBaseNextNewList, u, "rating_base_new_next")

        // This is the recent best
        // userRatingBaseHotList
        this.saveGeneralData(upsertUserAll.userRatingBaseHotList, u, "rating_base_hot_best")

        // userRatingBaseHotNextList
        this.saveGeneralData(upsertUserAll.userRatingBaseHotNextList, u, "rating_base_hot_next")


        // UserMusicDetailList
        val userMusicDetailList = upsertUserAll.userMusicDetailList
        val newUserMusicDetailList: MutableList<UserMusicDetail> = ArrayList()

        for (newUserMusicDetail in userMusicDetailList) {
            val musicId = newUserMusicDetail.musicId
            val level = newUserMusicDetail.level

            val musicDetailOptional =
                userMusicDetailRepository.findByUserAndMusicIdAndLevel(u, musicId, level)
            val userMusicDetail = musicDetailOptional.orElseGet {
                UserMusicDetail().apply { user = u }
            }

            newUserMusicDetail.id = userMusicDetail.id
            newUserMusicDetail.user = u
            newUserMusicDetailList.add(newUserMusicDetail)
        }
        userMusicDetailRepository.saveAll(newUserMusicDetailList)


        // UserCharacterList
        val userCharacterList = upsertUserAll.userCharacterList
        val newUserCharacterList: MutableList<UserCharacter> = ArrayList()

        for (newUserCharacter in userCharacterList) {
            val characterId = newUserCharacter.characterId

            val characterOptional = userCharacterRepository.findByUserAndCharacterId(u, characterId)
            val userCharacter = characterOptional.orElseGet {
                UserCharacter().apply { user = u }
            }

            newUserCharacter.id = userCharacter.id
            newUserCharacter.user = u
            newUserCharacterList.add(newUserCharacter)
        }
        userCharacterRepository.saveAll(newUserCharacterList)

        // UserCardList
        val userCardList = upsertUserAll.userCardList
        val newUserCardList: MutableList<UserCard> = ArrayList()

        for (newUserCard in userCardList) {
            val cardId = newUserCard.cardId

            val cardOptional = userCardRepository.findByUserAndCardId(u, cardId)
            val userCard = cardOptional.orElseGet { UserCard().apply { user = u } }

            newUserCard.id = userCard.id
            newUserCard.user = u
            newUserCardList.add(newUserCard)
        }
        userCardRepository.saveAll(newUserCardList)


        // UserDeckList
        val userDeckList = upsertUserAll.userDeckList
        val newUserDeckList: MutableList<UserDeck> = ArrayList()

        for (newUserDeck in userDeckList) {
            val deckId = newUserDeck.deckId

            val deckOptional = userDeckRepository.findByUserAndDeckId(u, deckId)
            val userDeck = deckOptional.orElseGet { UserDeck().apply { user = u } }

            newUserDeck.id = userDeck.id
            newUserDeck.user = u
            newUserDeckList.add(newUserDeck)
        }
        userDeckRepository.saveAll(newUserDeckList)


        // userTrainingRoomList
        val userTrainingRoomList = upsertUserAll.userTrainingRoomList
        val newUserTrainingRoomList: MutableList<UserTrainingRoom> = ArrayList()

        for (newUserTrainingRoom in userTrainingRoomList) {
            val roomId = newUserTrainingRoom.roomId

            val trainingRoomOptional = userTrainingRoomRepository.findByUserAndRoomId(u, roomId)
            val trainingRoom = trainingRoomOptional.orElseGet { UserTrainingRoom().apply { user = u } }

            newUserTrainingRoom.id = trainingRoom.id
            newUserTrainingRoom.user = u
            newUserTrainingRoomList.add(newUserTrainingRoom)
        }
        userTrainingRoomRepository.saveAll(newUserTrainingRoomList)


        // UserStoryList
        val userStoryList = upsertUserAll.userStoryList
        val newUserStoryList: MutableList<UserStory> = ArrayList()

        for (newUserStory in userStoryList) {
            val storyId = newUserStory.storyId

            val storyOptional = userStoryRepository.findByUserAndStoryId(u, storyId)
            val userStory = storyOptional.orElseGet { UserStory().apply { user = u } }

            newUserStory.id = userStory.id
            newUserStory.user = u
            newUserStoryList.add(newUserStory)
        }
        userStoryRepository.saveAll(newUserStoryList)


        // UserChapterList
        val userChapterList = upsertUserAll.userChapterList
        val newUserChapterList: MutableList<UserChapter> = ArrayList()

        for (newUserChapter in userChapterList) {
            val chapterId = newUserChapter.chapterId

            val chapterOptional = userChapterRepository.findByUserAndChapterId(u, chapterId)
            val userChapter = chapterOptional.orElseGet { UserChapter().apply { user = u } }

            newUserChapter.id = userChapter.id
            newUserChapter.user = u
            newUserChapterList.add(newUserChapter)
        }
        userChapterRepository.saveAll(newUserChapterList)


        // UserMemoryChapterList
        val userMemoryChapterList = upsertUserAll.userMemoryChapterList

        if (userMemoryChapterList != null) {
            val newUserMemoryChapterList: MutableList<UserMemoryChapter> = ArrayList()

            for (newUserMemoryChapter in userMemoryChapterList) {
                val chapterId = newUserMemoryChapter.chapterId

                val chapterOptional = userMemoryChapterRepository.findByUserAndChapterId(u, chapterId)
                val userChapter = chapterOptional.orElseGet { UserMemoryChapter().apply { user = u } }

                newUserMemoryChapter.id = userChapter.id
                newUserMemoryChapter.user = u
                newUserMemoryChapterList.add(newUserMemoryChapter)
            }
            userMemoryChapterRepository.saveAll(newUserMemoryChapterList)
        }

        // UserItemList
        val userItemList = upsertUserAll.userItemList
        val newUserItemList: MutableList<UserItem> = ArrayList()

        for (newUserItem in userItemList) {
            val itemKind = newUserItem.itemKind
            val itemId = newUserItem.itemId

            val itemOptional = userItemRepository.findByUserAndItemKindAndItemId(u, itemKind, itemId)
            val userItem = itemOptional.orElseGet { UserItem().apply { user = u } }

            newUserItem.id = userItem.id
            newUserItem.user = u
            newUserItemList.add(newUserItem)
        }
        userItemRepository.saveAll(newUserItemList)

        // UserMusicItemList
        val userMusicItemList = upsertUserAll.userMusicItemList
        val newUserMusicItemList: MutableList<UserMusicItem> = ArrayList()

        for (newUserMusicItem in userMusicItemList) {
            val musicId = newUserMusicItem.musicId

            val musicItemOptional = userMusicItemRepository.findByUserAndMusicId(u, musicId)
            val userMusicItem = musicItemOptional.orElseGet { UserMusicItem().apply { user = u } }

            newUserMusicItem.id = userMusicItem.id
            newUserMusicItem.user = u
            newUserMusicItemList.add(newUserMusicItem)
        }
        userMusicItemRepository.saveAll(newUserMusicItemList)


        // userLoginBonusList
        val userLoginBonusList = upsertUserAll.userLoginBonusList
        val newUserLoginBonusList: MutableList<UserLoginBonus> = ArrayList()

        for (newUserLoginBonus in userLoginBonusList) {
            val bonusId = newUserLoginBonus.bonusId

            val loginBonusOptional = userLoginBonusRepository.findByUserAndBonusId(u, bonusId)
            val userLoginBonus = loginBonusOptional.orElseGet {
                UserLoginBonus().apply { user = u }
            }

            newUserLoginBonus.id = userLoginBonus.id
            newUserLoginBonus.user = u
            newUserLoginBonusList.add(newUserLoginBonus)
        }
        userLoginBonusRepository.saveAll(newUserLoginBonusList)


        // UserEventPointList
        val userEventPointList = upsertUserAll.userEventPointList
        val newUserEventPointList: MutableList<UserEventPoint> = ArrayList()

        for (newUserEventPoint in userEventPointList) {
            val eventId = newUserEventPoint.eventId

            val eventPointOptional = userEventPointRepository.findByUserAndEventId(u, eventId)
            val userEventPoint = eventPointOptional.orElseGet { UserEventPoint().apply { user = u } }

            newUserEventPoint.id = userEventPoint.id
            newUserEventPoint.user = u
            newUserEventPointList.add(newUserEventPoint)
        }
        userEventPointRepository.saveAll(newUserEventPointList)


        // UserMissionPointList
        val userMissionPointList = upsertUserAll.userMissionPointList
        val newUserMissionPointList: MutableList<UserMissionPoint> = ArrayList()

        for (newUserMissionPoint in userMissionPointList) {
            val eventId = newUserMissionPoint.eventId

            val userMissionPointOptional = userMissionPointRepository.findByUserAndEventId(u, eventId)
            val userMissionPoint = userMissionPointOptional.orElseGet { UserMissionPoint().apply { user = u } }

            newUserMissionPoint.id = userMissionPoint.id
            newUserMissionPoint.user = u
            newUserMissionPointList.add(newUserMissionPoint)
        }
        userMissionPointRepository.saveAll(newUserMissionPointList)

        // UserRatinglogList (For the highest rating of each version)

        // UserBossList
        val userBossList = upsertUserAll.userBossList
        if (userBossList != null) {
            val newUserBossList: MutableList<UserBoss> = ArrayList()
            for (newUserBoss in userBossList) {
                val musicId = newUserBoss.musicId

                val userBossOptional = userBossRepository.findByUserAndMusicId(u, musicId)
                val userBoss = userBossOptional.orElseGet {
                    UserBoss().apply { user = u }
                }

                newUserBoss.id = userBoss.id
                newUserBoss.user = userBoss.user
                newUserBossList.add(newUserBoss)
            }
            userBossRepository.saveAll(newUserBossList)
        }

        // UserTechCountList
        val userTechCountList = upsertUserAll.userTechCountList
        if (userTechCountList != null) {
            val newUserTechCountList: MutableList<UserTechCount> = ArrayList()
            for (newUserTechCount in userTechCountList) {
                val levelId = newUserTechCount.levelId

                val userTechCountOptional = userTechCountRepository.findByUserAndLevelId(u, levelId)
                val userTechCount = userTechCountOptional.orElseGet { UserTechCount().apply { user = u } }

                newUserTechCount.id = userTechCount.id
                newUserTechCount.user = userTechCount.user
                newUserTechCountList.add(newUserTechCount)
            }
            userTechCountRepository.saveAll(newUserTechCountList)
        }

        // UserScenarioList
        val userScenarioList = upsertUserAll.userScenarioList
        if (userScenarioList != null) {
            val newUserScenarioList: MutableList<UserScenario> = ArrayList()
            for (newUserScenario in userScenarioList) {
                val scenarioId = newUserScenario.scenarioId

                val userScenarioOptional = userScenarioRepository.findByUserAndScenarioId(u, scenarioId)
                val userScenario = userScenarioOptional.orElseGet { UserScenario().apply { user = u } }

                newUserScenario.id = userScenario.id
                newUserScenario.user = userScenario.user
                newUserScenarioList.add(newUserScenario)
            }
            userScenarioRepository.saveAll(newUserScenarioList)
        }

        // UserTradeItemList
        val userTradeItemList = upsertUserAll.userTradeItemList
        val newUserTradeItemList: MutableList<UserTradeItem> = ArrayList()

        for (newUserTradeItem in userTradeItemList) {
            val chapterId = newUserTradeItem.chapterId
            val tradeItemId = newUserTradeItem.tradeItemId

            val tradeItemOptional =
                userTradeItemRepository.findByUserAndChapterIdAndTradeItemId(u, chapterId, tradeItemId)
            val userTradeItem = tradeItemOptional.orElseGet { UserTradeItem().apply { user = u } }

            newUserTradeItem.id = userTradeItem.id
            newUserTradeItem.user = u
            newUserTradeItemList.add(newUserTradeItem)
        }
        userTradeItemRepository.saveAll(newUserTradeItemList)

        // UserEventMusicList
        val userEventMusicList = upsertUserAll.userEventMusicList
        val newUserEventMusicList: MutableList<UserEventMusic> = ArrayList()

        for (newUserEventMusic in userEventMusicList) {
            val eventId = newUserEventMusic.eventId
            val type = newUserEventMusic.type
            val musicId = newUserEventMusic.musicId

            val eventMusicOptional =
                userEventMusicRepository.findByUserAndEventIdAndTypeAndMusicId(u, eventId, type, musicId)
            val userEventMusic = eventMusicOptional.orElseGet { UserEventMusic().apply { user = u } }

            newUserEventMusic.id = userEventMusic.id
            newUserEventMusic.user = u
            newUserEventMusicList.add(newUserEventMusic)
        }
        userEventMusicRepository.saveAll(newUserEventMusicList)

        // UserTechEventList
        val userTechEventList = upsertUserAll.userTechEventList
        val newUserTechEventList: MutableList<UserTechEvent> = ArrayList()

        for (newUserTechEvent in userTechEventList) {
            val eventId = newUserTechEvent.eventId

            val techEventOptional = userTechEventRepository.findByUserAndEventId(u, eventId)
            val userTechEvent = techEventOptional.orElseGet { UserTechEvent().apply { user = u } }

            newUserTechEvent.id = userTechEvent.id
            newUserTechEvent.user = u
            newUserTechEventList.add(newUserTechEvent)
        }
        userTechEventRepository.saveAll(newUserTechEventList)

        // UserKopList
        val userKopList = upsertUserAll.userKopList
        val newUserKopList: MutableList<UserKop> = ArrayList()

        for (newUserKop in userKopList) {
            val kopId = newUserKop.kopId
            val areaId = newUserKop.areaId

            val kopOptional = userKopRepository.findByUserAndKopIdAndAreaId(u, kopId, areaId)
            val userKop = kopOptional.orElseGet { UserKop().apply { user = u } }

            newUserKop.id = userKop.id
            newUserKop.user = u
            newUserKopList.add(newUserKop)
        }
        userKopRepository.saveAll(newUserKopList)

        val json = mapper.write(CodeResp(1, "upsertUserAll"))
        logger.info("Response: $json")
        return json
    }

    private fun saveGeneralData(itemList: List<UserRecentRating>, newUserData: UserData, key: String) {
        val sb = StringBuilder()
        // Convert to a string
        for (item in itemList) {
            sb.append(item.musicId).append(":").append(item.difficultId).append(":").append(item.score)
            sb.append(",")
        }
        if (sb.length > 0) {
            sb.deleteCharAt(sb.length - 1)
        }
        val uOptional = userGeneralDataRepository.findByUserAndPropertyKey(newUserData, key)
        val userGeneralData = uOptional.orElseGet {
            UserGeneralData().apply {
                user = newUserData
                propertyKey = key
            }
        }
        userGeneralData.propertyValue = sb.toString()
        userGeneralDataRepository.save(userGeneralData)
    }

    companion object {
        private val logger = logger()
    }
}
