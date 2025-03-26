package icu.samnyan.aqua.sega.ongeki.model

import com.fasterxml.jackson.annotation.JsonIgnore
import icu.samnyan.aqua.sega.ongeki.model.common.GpProductID
import icu.samnyan.aqua.sega.ongeki.model.common.ItemType
import jakarta.persistence.*
import java.io.Serializable

@Entity(name = "OngekiGameCard")
@Table(name = "ongeki_game_card")
class GameCard {
    @Id
    var id: Long = 0
    var name: String = ""
    var nickName: String = ""
    var attribute: String = ""
    var charaId = 0
    var school: String = ""
    var gakunen: String = ""
    var rarity: String = ""
    // csv
    var levelParam: String = ""
    var skillId = 0
    var choKaikaSkillId = 0
    var cardNumber: String = ""
    var version: String = ""
}


@Entity(name = "OngekiGameChara")
@Table(name = "ongeki_game_chara")
class GameChara {
    @Id
    var id: Long = 0
    var name: String = ""
    var cv: String = ""
    var modelId = 0
}


@Entity(name = "OngekiGameEvent")
@Table(name = "ongeki_game_event")
class GameEvent {
    @Id
    var id: Long = 0
}


@Entity(name = "OngekiGameMusic")
@Table(name = "ongeki_game_music")
class GameMusic {
    @Id
    var id: Long = 0
    var name: String = ""
    var sortName: String = ""
    var artistName: String = ""
    var genre: String = ""
    var bossCardId = 0
    var bossLevel = 0
    var level0: String = ""
    var level1: String = ""
    var level2: String = ""
    var level3: String = ""
    var level4: String = ""
}


@Entity(name = "OngekiGamePoint")
@Table(name = "ongeki_game_point", uniqueConstraints = [UniqueConstraint(columnNames = ["type"])])
class GamePoint(@field:Enumerated(EnumType.ORDINAL) var type: GpProductID, var cost: Int) :
    Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long = 0
    val startDate = "2000-01-01 05:00:00.0"
    val endDate = "2099-01-01 05:00:00.0"
}


@Entity(name = "OngekiGamePresent")
@Table(name = "ongeki_game_present")
class GamePresent(
    presentId: Int, var presentName: String, var rewardId: Int, // count
    var stock: Int, // acquisitionCondition
    var message: String
) :
    Serializable {
    @Id
    @Column(name = "id")
    var presentId = presentId.toLong()
    val startDate = "2000-01-01 05:00:00.0"
    val endDate = "2099-01-01 05:00:00.0"
}


@Entity(name = "OngekiGameReward")
@Table(name = "ongeki_game_reward")
class GameReward(
    rewardId: Int,
    @field:Enumerated(EnumType.ORDINAL) var itemKind: ItemType,
    var itemId: Int
) {
    @Id
    @Column(name = "id")
    var rewardId = rewardId.toLong()
}


@Entity(name = "OngekiGameSkill")
@Table(name = "ongeki_game_skill")
class GameSkill {
    @Id
    var id: Long = 0
    var name: String = ""
    var category: String = ""
    var info: String = ""
}
