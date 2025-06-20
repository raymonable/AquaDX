package icu.samnyan.aqua.sega.chusan.model.userdata;

import icu.samnyan.aqua.net.games.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import java.time.LocalDateTime


@Entity(name = "ChusanTeam")
@Table(name = "chusan_teams")
class Chu3Team {
    @Id
    @Column(name = "team_id")
    var id: Long = 0
    var ownerAuId: Long = 0
    var name: String = ""

    var ranking = 0
    var points = 0
    var membersCount = 0
}

@Entity(name = "ChusanTeamRequest")
@Table(name = "chusan_team_requests")
class Chu3TeamRequest {
    @Id
    @Column(name = "request_id")
    var id: Long = 0
    var team_id: Long = 0
    var request_au_id: Long = 0
    var request_time: LocalDateTime = LocalDateTime.now()
}