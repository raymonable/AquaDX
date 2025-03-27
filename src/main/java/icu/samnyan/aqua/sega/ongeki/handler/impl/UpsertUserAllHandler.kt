package icu.samnyan.aqua.sega.ongeki.handler.impl

import ext.logger
import icu.samnyan.aqua.sega.general.BaseHandler
import icu.samnyan.aqua.sega.general.model.response.UserRecentRating
import icu.samnyan.aqua.sega.general.service.CardService
import icu.samnyan.aqua.sega.ongeki.OngekiUserRepos
import icu.samnyan.aqua.sega.ongeki.model.*
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
    val db: OngekiUserRepos
) : BaseHandler {
    override fun handle(request: Map<String, Any>): String? {
        val userId = (request["userId"] as Number).toLong()
        val all = mapper.convert(
            request["upsertUserAll"]!!,
            UpsertUserAll::class.java
        )

        // All the field should exist, no need to check now.
        // UserData
        val u: UserData
        run {
            val userData: UserData
            val userOptional = db.data.findByCard_ExtId(userId)

            // UserData might be empty on later runs
            if (userOptional.isEmpty && all.userData.isNullOrEmpty()) {
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
            u = if (!all.userData.isNullOrEmpty()) all.userData!![0] else userData

            u.id = userData.id
            u.card = userData.card

            // Set eventWatchedDate with lastPlayDate, because client doesn't update it
            u.eventWatchedDate = userData.lastPlayDate
            u.cmEventWatchedDate = userData.lastPlayDate
            db.data.save(u)
        }


        // UserOption
        val newUserOption = all.userOption!![0]

        val userOptionOptional = db.option.findSingleByUser(u)
        val userOption = userOptionOptional.orElseGet { UserOption().apply { user = u } }

        newUserOption.id = userOption.id
        newUserOption.user = userOption.user

        db.option.save(newUserOption)


        // UserPlaylogList
        val userPlaylogList = all.userPlaylogList
        val newUserPlaylogList: MutableList<UserPlaylog> = ArrayList()

        if (userPlaylogList != null) {
            for (newUserPlaylog in userPlaylogList) {
                newUserPlaylog.user = u
                newUserPlaylogList.add(newUserPlaylog)
            }
        }

        db.playlog.saveAll(newUserPlaylogList)


        // UserSessionlogList, UserJewelboostlogLost doesn't need to be saved for a private server


        // UserActivityList
        val userActivityList = all.userActivityList
        val newUserActivityList: MutableList<UserActivity> = ArrayList()

        if (userActivityList != null) {
            for (newUserActivity in userActivityList) {
                val kind = newUserActivity.kind
                val id = newUserActivity.activityId

                if (kind != 0 && id != 0) {
                    val activityOptional = db.activity.findByUserAndKindAndActivityId(u, kind, id)
                    val userActivity = activityOptional.orElseGet {
                        UserActivity().apply { user = u }
                    }

                    newUserActivity.id = userActivity.id
                    newUserActivity.user = u
                    newUserActivityList.add(newUserActivity)
                }
            }
        }
        newUserActivityList.sortWith { a, b -> b.sortNumber.compareTo(a.sortNumber) }
        db.activity.saveAll(newUserActivityList)


        // UserRecentRatingList
        // This thing still need to save to solve the rating drop
        all.userRecentRatingList?.let { this.saveRecentRatingData(it, u, "recent_rating_list") }

        /*
         * The rating and battle point calculation is little bit complex.
         * So I just create a UserGeneralData class to store this value
         * into a csv format for convenience
         */
        // UserBpBaseList (For calculating Battle point)
        all.userBpBaseList?.let { this.saveRecentRatingData(it, u, "battle_point_base") }

        // This is the best rating of all charts. Best 30 + 10 after that.
        // userRatingBaseBestList
        all.userRatingBaseBestList?.let { this.saveRecentRatingData(it, u, "rating_base_best") }

        // userRatingBaseNextList
        all.userRatingBaseNextList?.let { this.saveRecentRatingData(it, u, "rating_base_next") }

        // This is the best rating of new charts. Best 15 + 10 after that.
        // New chart means same version
        // userRatingBaseBestNewList
        all.userRatingBaseBestNewList?.let { this.saveRecentRatingData(it, u, "rating_base_new_best") }

        // userRatingBaseNextNewList
        all.userRatingBaseNextNewList?.let { this.saveRecentRatingData(it, u, "rating_base_new_next") }

        // This is the recent best
        // userRatingBaseHotList
        all.userRatingBaseHotList?.let { this.saveRecentRatingData(it, u, "rating_base_hot_best") }

        // userRatingBaseHotNextList
        all.userRatingBaseHotNextList?.let { this.saveRecentRatingData(it, u, "rating_base_hot_next") }

        // Re:Fresh
        all.userNewRatingBaseBestList?.let { this.saveFumenScoreData(it, u, "new_rating_base_best") }
        all.userNewRatingBaseNextBestList?.let { this.saveFumenScoreData(it, u, "new_rating_base_next_best") }
        all.userNewRatingBaseBestNewList?.let { this.saveFumenScoreData(it, u, "new_rating_base_new_best") }
        all.userNewRatingBaseNextBestNewList?.let { this.saveFumenScoreData(it, u, "new_rating_base_new_next_best") }
        all.userNewRatingBasePScoreList?.let { this.saveFumenScoreData(it, u, "new_rating_base_pscore") }
        all.userNewRatingBaseNextPScoreList?.let { this.saveFumenScoreData(it, u, "new_rating_base_next_pscore") }

        // UserMusicDetailList
        val userMusicDetailList = all.userMusicDetailList
        val newUserMusicDetailList: MutableList<UserMusicDetail> = ArrayList()

        if (userMusicDetailList != null) {
            for (newUserMusicDetail in userMusicDetailList) {
                val musicId = newUserMusicDetail.musicId
                val level = newUserMusicDetail.level

                val musicDetailOptional =
                    db.musicDetail.findByUserAndMusicIdAndLevel(u, musicId, level)
                val userMusicDetail = musicDetailOptional.orElseGet {
                    UserMusicDetail().apply { user = u }
                }

                newUserMusicDetail.id = userMusicDetail.id
                newUserMusicDetail.user = u
                newUserMusicDetailList.add(newUserMusicDetail)
            }
        }
        db.musicDetail.saveAll(newUserMusicDetailList)


        // UserCharacterList
        val userCharacterList = all.userCharacterList
        val newUserCharacterList: MutableList<UserCharacter> = ArrayList()

        if (userCharacterList != null) {
            for (newUserCharacter in userCharacterList) {
                val characterId = newUserCharacter.characterId

                val characterOptional = db.character.findByUserAndCharacterId(u, characterId)
                val userCharacter = characterOptional.orElseGet {
                    UserCharacter().apply { user = u }
                }

                newUserCharacter.id = userCharacter.id
                newUserCharacter.user = u
                newUserCharacterList.add(newUserCharacter)
            }
        }
        db.character.saveAll(newUserCharacterList)

        // UserCardList
        val userCardList = all.userCardList
        val newUserCardList: MutableList<UserCard> = ArrayList()

        if (userCardList != null) {
            for (newUserCard in userCardList) {
                val cardId = newUserCard.cardId

                val cardOptional = db.card.findByUserAndCardId(u, cardId)
                val userCard = cardOptional.orElseGet { UserCard().apply { user = u } }

                newUserCard.id = userCard.id
                newUserCard.user = u
                newUserCardList.add(newUserCard)
            }
        }
        db.card.saveAll(newUserCardList)


        // UserDeckList
        val userDeckList = all.userDeckList
        val newUserDeckList: MutableList<UserDeck> = ArrayList()

        if (userDeckList != null) {
            for (newUserDeck in userDeckList) {
                val deckId = newUserDeck.deckId

                val deckOptional = db.deck.findByUserAndDeckId(u, deckId)
                val userDeck = deckOptional.orElseGet { UserDeck().apply { user = u } }

                newUserDeck.id = userDeck.id
                newUserDeck.user = u
                newUserDeckList.add(newUserDeck)
            }
        }
        db.deck.saveAll(newUserDeckList)


        // userTrainingRoomList
        val userTrainingRoomList = all.userTrainingRoomList
        val newUserTrainingRoomList: MutableList<UserTrainingRoom> = ArrayList()

        if (userTrainingRoomList != null) {
            for (newUserTrainingRoom in userTrainingRoomList) {
                val roomId = newUserTrainingRoom.roomId

                val trainingRoomOptional = db.trainingRoom.findByUserAndRoomId(u, roomId)
                val trainingRoom = trainingRoomOptional.orElseGet { UserTrainingRoom().apply { user = u } }

                newUserTrainingRoom.id = trainingRoom.id
                newUserTrainingRoom.user = u
                newUserTrainingRoomList.add(newUserTrainingRoom)
            }
        }
        db.trainingRoom.saveAll(newUserTrainingRoomList)


        // UserStoryList
        val userStoryList = all.userStoryList
        val newUserStoryList: MutableList<UserStory> = ArrayList()

        if (userStoryList != null) {
            for (newUserStory in userStoryList) {
                val storyId = newUserStory.storyId

                val storyOptional = db.story.findByUserAndStoryId(u, storyId)
                val userStory = storyOptional.orElseGet { UserStory().apply { user = u } }

                newUserStory.id = userStory.id
                newUserStory.user = u
                newUserStoryList.add(newUserStory)
            }
        }
        db.story.saveAll(newUserStoryList)


        // UserChapterList
        val userChapterList = all.userChapterList
        val newUserChapterList: MutableList<UserChapter> = ArrayList()

        if (userChapterList != null) {
            for (newUserChapter in userChapterList) {
                val chapterId = newUserChapter.chapterId

                val chapterOptional = db.chapter.findByUserAndChapterId(u, chapterId)
                val userChapter = chapterOptional.orElseGet { UserChapter().apply { user = u } }

                newUserChapter.id = userChapter.id
                newUserChapter.user = u
                newUserChapterList.add(newUserChapter)
            }
        }
        db.chapter.saveAll(newUserChapterList)


        // UserMemoryChapterList
        val userMemoryChapterList = all.userMemoryChapterList

        if (userMemoryChapterList != null) {
            val newUserMemoryChapterList: MutableList<UserMemoryChapter> = ArrayList()

            for (newUserMemoryChapter in userMemoryChapterList) {
                val chapterId = newUserMemoryChapter.chapterId

                val chapterOptional = db.memoryChapter.findByUserAndChapterId(u, chapterId)
                val userChapter = chapterOptional.orElseGet { UserMemoryChapter().apply { user = u } }

                newUserMemoryChapter.id = userChapter.id
                newUserMemoryChapter.user = u
                newUserMemoryChapterList.add(newUserMemoryChapter)
            }
            db.memoryChapter.saveAll(newUserMemoryChapterList)
        }

        // UserItemList
        val userItemList = all.userItemList
        val newUserItemList: MutableList<UserItem> = ArrayList()

        if (userItemList != null) {
            for (newUserItem in userItemList) {
                val itemKind = newUserItem.itemKind
                val itemId = newUserItem.itemId

                val itemOptional = db.item.findByUserAndItemKindAndItemId(u, itemKind, itemId)
                val userItem = itemOptional.orElseGet { UserItem().apply { user = u } }

                newUserItem.id = userItem.id
                newUserItem.user = u
                newUserItemList.add(newUserItem)
            }
        }
        db.item.saveAll(newUserItemList)

        // UserMusicItemList
        val userMusicItemList = all.userMusicItemList
        val newUserMusicItemList: MutableList<UserMusicItem> = ArrayList()

        if (userMusicItemList != null) {
            for (newUserMusicItem in userMusicItemList) {
                val musicId = newUserMusicItem.musicId

                val musicItemOptional = db.musicItem.findByUserAndMusicId(u, musicId)
                val userMusicItem = musicItemOptional.orElseGet { UserMusicItem().apply { user = u } }

                newUserMusicItem.id = userMusicItem.id
                newUserMusicItem.user = u
                newUserMusicItemList.add(newUserMusicItem)
            }
        }
        db.musicItem.saveAll(newUserMusicItemList)


        // userLoginBonusList
        val userLoginBonusList = all.userLoginBonusList
        val newUserLoginBonusList: MutableList<UserLoginBonus> = ArrayList()

        if (userLoginBonusList != null) {
            for (newUserLoginBonus in userLoginBonusList) {
                val bonusId = newUserLoginBonus.bonusId

                val loginBonusOptional = db.loginBonus.findByUserAndBonusId(u, bonusId)
                val userLoginBonus = loginBonusOptional.orElseGet {
                    UserLoginBonus().apply { user = u }
                }

                newUserLoginBonus.id = userLoginBonus.id
                newUserLoginBonus.user = u
                newUserLoginBonusList.add(newUserLoginBonus)
            }
        }
        db.loginBonus.saveAll(newUserLoginBonusList)


        // UserEventPointList
        val userEventPointList = all.userEventPointList
        val newUserEventPointList: MutableList<UserEventPoint> = ArrayList()

        if (userEventPointList != null) {
            for (newUserEventPoint in userEventPointList) {
                val eventId = newUserEventPoint.eventId

                val eventPointOptional = db.eventPoint.findByUserAndEventId(u, eventId)
                val userEventPoint = eventPointOptional.orElseGet { UserEventPoint().apply { user = u } }

                newUserEventPoint.id = userEventPoint.id
                newUserEventPoint.user = u
                newUserEventPointList.add(newUserEventPoint)
            }
        }
        db.eventPoint.saveAll(newUserEventPointList)


        // UserMissionPointList
        val userMissionPointList = all.userMissionPointList
        val newUserMissionPointList: MutableList<UserMissionPoint> = ArrayList()

        if (userMissionPointList != null) {
            for (newUserMissionPoint in userMissionPointList) {
                val eventId = newUserMissionPoint.eventId

                val userMissionPointOptional = db.missionPoint.findByUserAndEventId(u, eventId)
                val userMissionPoint = userMissionPointOptional.orElseGet { UserMissionPoint().apply { user = u } }

                newUserMissionPoint.id = userMissionPoint.id
                newUserMissionPoint.user = u
                newUserMissionPointList.add(newUserMissionPoint)
            }
        }
        db.missionPoint.saveAll(newUserMissionPointList)

        // UserRatinglogList (For the highest rating of each version)

        // UserBossList
        val userBossList = all.userBossList
        if (userBossList != null) {
            val newUserBossList: MutableList<UserBoss> = ArrayList()
            for (newUserBoss in userBossList) {
                val musicId = newUserBoss.musicId

                val userBossOptional = db.boss.findByUserAndMusicId(u, musicId)
                val userBoss = userBossOptional.orElseGet {
                    UserBoss().apply { user = u }
                }

                newUserBoss.id = userBoss.id
                newUserBoss.user = userBoss.user
                newUserBossList.add(newUserBoss)
            }
            db.boss.saveAll(newUserBossList)
        }

        // UserTechCountList
        val userTechCountList = all.userTechCountList
        if (userTechCountList != null) {
            val newUserTechCountList: MutableList<UserTechCount> = ArrayList()
            for (newUserTechCount in userTechCountList) {
                val levelId = newUserTechCount.levelId

                val userTechCountOptional = db.techCount.findByUserAndLevelId(u, levelId)
                val userTechCount = userTechCountOptional.orElseGet { UserTechCount().apply { user = u } }

                newUserTechCount.id = userTechCount.id
                newUserTechCount.user = userTechCount.user
                newUserTechCountList.add(newUserTechCount)
            }
            db.techCount.saveAll(newUserTechCountList)
        }

        // UserScenarioList
        val userScenarioList = all.userScenarioList
        if (userScenarioList != null) {
            val newUserScenarioList: MutableList<UserScenario> = ArrayList()
            for (newUserScenario in userScenarioList) {
                val scenarioId = newUserScenario.scenarioId

                val userScenarioOptional = db.scenario.findByUserAndScenarioId(u, scenarioId)
                val userScenario = userScenarioOptional.orElseGet { UserScenario().apply { user = u } }

                newUserScenario.id = userScenario.id
                newUserScenario.user = userScenario.user
                newUserScenarioList.add(newUserScenario)
            }
            db.scenario.saveAll(newUserScenarioList)
        }

        // UserTradeItemList
        val userTradeItemList = all.userTradeItemList
        val newUserTradeItemList: MutableList<UserTradeItem> = ArrayList()

        if (userTradeItemList != null) {
            for (newUserTradeItem in userTradeItemList) {
                val chapterId = newUserTradeItem.chapterId
                val tradeItemId = newUserTradeItem.tradeItemId

                val tradeItemOptional =
                    db.tradeItem.findByUserAndChapterIdAndTradeItemId(u, chapterId, tradeItemId)
                val userTradeItem = tradeItemOptional.orElseGet { UserTradeItem().apply { user = u } }

                newUserTradeItem.id = userTradeItem.id
                newUserTradeItem.user = u
                newUserTradeItemList.add(newUserTradeItem)
            }
        }
        db.tradeItem.saveAll(newUserTradeItemList)

        // UserEventMusicList
        val userEventMusicList = all.userEventMusicList
        val newUserEventMusicList: MutableList<UserEventMusic> = ArrayList()

        if (userEventMusicList != null) {
            for (newUserEventMusic in userEventMusicList) {
                val eventId = newUserEventMusic.eventId
                val type = newUserEventMusic.type
                val musicId = newUserEventMusic.musicId

                val eventMusicOptional =
                    db.eventMusic.findByUserAndEventIdAndTypeAndMusicId(u, eventId, type, musicId)
                val userEventMusic = eventMusicOptional.orElseGet { UserEventMusic().apply { user = u } }

                newUserEventMusic.id = userEventMusic.id
                newUserEventMusic.user = u
                newUserEventMusicList.add(newUserEventMusic)
            }
        }
        db.eventMusic.saveAll(newUserEventMusicList)

        // UserTechEventList
        val userTechEventList = all.userTechEventList
        val newUserTechEventList: MutableList<UserTechEvent> = ArrayList()

        if (userTechEventList != null) {
            for (newUserTechEvent in userTechEventList) {
                val eventId = newUserTechEvent.eventId

                val techEventOptional = db.techEvent.findByUserAndEventId(u, eventId)
                val userTechEvent = techEventOptional.orElseGet { UserTechEvent().apply { user = u } }

                newUserTechEvent.id = userTechEvent.id
                newUserTechEvent.user = u
                newUserTechEventList.add(newUserTechEvent)
            }
        }
        db.techEvent.saveAll(newUserTechEventList)

        // UserKopList
        val userKopList = all.userKopList
        val newUserKopList: MutableList<UserKop> = ArrayList()

        if (userKopList != null) {
            for (newUserKop in userKopList) {
                val kopId = newUserKop.kopId
                val areaId = newUserKop.areaId

                val kopOptional = db.kop.findByUserAndKopIdAndAreaId(u, kopId, areaId)
                val userKop = kopOptional.orElseGet { UserKop().apply { user = u } }

                newUserKop.id = userKop.id
                newUserKop.user = u
                newUserKopList.add(newUserKop)
            }
        }
        db.kop.saveAll(newUserKopList)

        // UserEventMap
        val newUserEventMap = all.userEventMap
        if (newUserEventMap != null) {
            val userEventOptional = db.eventMap.findSingleByUser(u)
            val userEventMap: UserEventMap = userEventOptional.orElseGet { UserEventMap().apply { user = u } }

            newUserEventMap.id = userEventMap.id
            newUserEventMap.user = u
            db.eventMap.save(newUserEventMap)
        }

        val json = mapper.write(CodeResp(1, "upsertUserAll"))
        logger.info("Response: $json")
        return json
    }

    private fun saveRecentRatingData(itemList: List<UserRecentRating>, newUserData: UserData, key: String) {
        val sb = StringBuilder()
        // Convert to a string
        for (item in itemList) {
            sb.append(item.musicId).append(":").append(item.difficultId).append(":").append(item.score)
            sb.append(",")
        }
        if (sb.isNotEmpty()) { sb.deleteCharAt(sb.length - 1) }
        saveGeneralData(newUserData, key, sb.toString())
    }

    private fun saveFumenScoreData(itemList: List<OngekiFumenScore>, newUserData: UserData, key: String) {
        val sb = StringBuilder()
        for (item in itemList) {
            sb.append(item.musicId).append(":")
                .append(item.difficultId).append(":")
                .append(item.score).append(":")
                .append(item.platinumScoreStar).append(":")
                .append(item.platinumScoreMax)
            sb.append(",")
        }

        if (sb.isNotEmpty()) { sb.deleteCharAt(sb.length - 1) }
        saveGeneralData(newUserData, key, sb.toString())
    }

    private fun saveGeneralData(newUserData: UserData, key: String, data: String) {
        val uOptional = db.generalData.findByUserAndPropertyKey(newUserData, key)
        val userGeneralData = uOptional.orElseGet {
            UserGeneralData().apply {
                user = newUserData
                propertyKey = key
            }
        }
        userGeneralData.propertyValue = data
        db.generalData.save(userGeneralData)
    }

    companion object {
        private val logger = logger()
    }
}
