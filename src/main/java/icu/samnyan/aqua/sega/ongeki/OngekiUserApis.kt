package icu.samnyan.aqua.sega.ongeki

import ext.empty
import ext.int
import ext.invoke
import ext.parsing
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun OngekiController.initUser() {
    "GetUserData" { mapOf("userId" to uid, "userData" to db.data.findByCard_ExtId(uid)()) }
    "GetUserOption" { mapOf("userId" to uid, "userOption" to db.option.findSingleByUser_Card_ExtId(uid)()) }
    "GetUserEventMap" { mapOf("userId" to uid, "userEventMap" to db.eventMap.findSingleByUser_Card_ExtId(uid)()) }

    "GetUserTechEvent".unpaged { db.techEvent.findByUser_Card_ExtId(uid) }
    "GetUserBoss".unpaged { db.boss.findByUser_Card_ExtId(uid) }
    "GetUserCard".unpaged { db.card.findByUser_Card_ExtId(uid) }
    "GetUserChapter".unpaged { db.chapter.findByUser_Card_ExtId(uid) }
    "GetUserMemoryChapter".unpaged { db.memoryChapter.findByUser_Card_ExtId(uid) }
    "GetUserCharacter".unpaged { db.character.findByUser_Card_ExtId(uid) }
    "GetUserDeckByKey".unpaged { db.deck.findByUser_Card_ExtId(uid) }
    "GetUserEventMusic".unpaged { db.eventMusic.findByUser_Card_ExtId(uid) }
    "GetUserEventPoint".unpaged { db.eventPoint.findByUser_Card_ExtId(uid) }
    "GetUserLoginBonus".unpaged { db.loginBonus.findByUser_Card_ExtId(uid) }
    "GetUserMissionPoint".unpaged { db.missionPoint.findByUser_Card_ExtId(uid) }
    "GetUserMusicItem".unpaged { db.musicItem.findByUser_Card_ExtId(uid) }

    val dtPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")

    "GetUserTechEventRanking".unpaged {
        val time = LocalDateTime.now().format(dtPattern)

        db.techEvent.findByUser_Card_ExtId(uid).map { mapOf(
            "eventId" to it.eventId,
            "date" to time,
            "rank" to 1,
            "totalTechScore" to it.totalTechScore,
            "totalPlatinumScore" to it.totalPlatinumScore
        ) }
    }

    "GetUserEventRanking".unpaged {
        val time = LocalDateTime.now().format(dtPattern)

        db.eventPoint.findByUser_Card_ExtId(uid).map { mapOf(
            "eventId" to it.eventId,
            "type" to 1, // Latest ranking
            "date" to time,
            "rank" to db.eventPoint.calculateRankByUserAndEventId(uid, it.eventId),
            "point" to it.point
        ) }
    }

    "GetUserActivity".unpagedExtra {
        val kind = parsing { data["kind"]!!.int }
        db.activity.findByUser_Card_ExtIdAndKindOrderBySortNumberDesc(uid, kind)
            .take(if (kind == 1) 15 else 10) to mapOf("kind" to kind)
    }

    "GetUserBpBase".unpaged { empty }
}