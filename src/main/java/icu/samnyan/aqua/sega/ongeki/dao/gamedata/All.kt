package icu.samnyan.aqua.sega.ongeki.dao.gamedata

import icu.samnyan.aqua.sega.ongeki.model.gamedata.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

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
