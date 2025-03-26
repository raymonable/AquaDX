package icu.samnyan.aqua.sega.ongeki.model.userdata

import icu.samnyan.aqua.sega.util.jackson.BasicMapper
import kotlin.io.path.Path
import kotlin.io.path.writeText

fun main(args: Array<String>) {
    val classes = listOf(UserActivity(), UserBoss(), UserCard(), UserChapter(), UserCharacter(), UserData(), UserDeck(), UserEventMusic(), UserEventPoint(), UserGeneralData(), UserItem(), UserKop(), UserLoginBonus(), UserMemoryChapter(), UserMissionPoint(), UserMusicDetail(), UserMusicItem(), UserOption(), UserPlaylog(), UserRival(), UserScenario(), UserStory(), UserTechCount(), UserTechEvent(), UserTradeItem(), UserTrainingRoom())
    val mapper = BasicMapper()

    // Json stringify all of them
    var str = ""
    classes.forEach {
        str += mapper.write(it) + "\n"
    }
    Path("ogk-userdata.jsonl").writeText(str)
}