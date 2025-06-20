package icu.samnyan.aqua.net.games.chu3

import ext.*
import icu.samnyan.aqua.net.db.AquaUserServices
import icu.samnyan.aqua.net.games.*
import icu.samnyan.aqua.net.utils.*
import icu.samnyan.aqua.sega.chusan.ChusanProps
import icu.samnyan.aqua.sega.chusan.model.*
import icu.samnyan.aqua.sega.chusan.model.userdata.Chu3Team
import icu.samnyan.aqua.sega.chusan.model.userdata.Chu3TeamRequest
import icu.samnyan.aqua.sega.chusan.model.userdata.Chu3UserData
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@RestController
@API("api/v2/game/chu3")
class Chusan(
    override val us: AquaUserServices,
    override val playlogRepo: Chu3UserPlaylogRepo,
    override val userDataRepo: Chu3UserDataRepo,
    override val userMusicRepo: Chu3UserMusicDetailRepo,
    val props: ChusanProps,
    val rp: Chu3Repos
): GameApiController<Chu3UserData>("chu3", Chu3UserData::class) {
    override suspend fun trend(@RP username: Str): List<TrendOut> = us.cardByName(username) { card ->
        findTrend(playlogRepo.findByUserCardExtId(card.extId)
            .map { TrendLog(it.playDate.toString(), it.playerRating) })
    }

    // Only show > AAA rank
    override val shownRanks = chu3Scores.filter { it.first >= 95 * 10000 }
    override val settableFields: Map<String, (Chu3UserData, String) -> Unit> by lazy { mapOf(
        "userName" to usernameCheck(SEGA_USERNAME_CAHRS),
        "nameplateId" to { u, v -> u.nameplateId = v.int },
        "frameId" to { u, v -> u.frameId = v.int },
        "trophyId" to { u, v -> u.trophyId = v.int },
        "trophyIdSub1" to { u, v -> u.trophyIdSub1 = v.int },
        "trophyIdSub2" to { u, v -> u.trophyIdSub2 = v.int },
        "mapIconId" to { u, v -> u.mapIconId = v.int },
        "voiceId" to { u, v -> u.voiceId = v.int },
        "avatarWear" to { u, v -> u.avatarWear = v.int },
        "avatarHead" to { u, v -> u.avatarHead = v.int },
        "avatarFace" to { u, v -> u.avatarFace = v.int },
        "avatarSkin" to { u, v -> u.avatarSkin = v.int },
        "avatarItem" to { u, v -> u.avatarItem = v.int },
        "avatarFront" to { u, v -> u.avatarFront = v.int },
        "avatarBack" to { u, v -> u.avatarBack = v.int },

        "lastRomVersion" to { u, v -> u.lastRomVersion = v },
        "lastDataVersion" to { u, v -> u.lastDataVersion = v },
    ) }
    override val gettableFields: Set<String> = setOf("level", "playerRating", "characterId")

    override suspend fun userSummary(@RP username: Str, @RP token: String?) = us.cardByName(username) { card ->
        // Summary values: total plays, player rating, server-wide ranking
        // number of each rank, max combo, number of full combo, number of all perfect
        val extra = rp.userGeneralData.findByUser_Card_ExtId(card.extId)
            .associate { it.propertyKey to it.propertyValue }

        val ratingComposition = mapOf(
            "recent10" to (extra["recent_rating_list"] ?: ""),
            "best30" to (extra["rating_base_list"] ?: ""),
            "hot10" to (extra["rating_hot_list"] ?: ""),
            "next10" to (extra["rating_next_list"] ?: ""),
            "new" to (extra["rating_new_list"] ?: ""),
        )

        genericUserSummary(card, ratingComposition)
    }

    /**
     * Added by Clansty for Bot
     * TODO: Reduce redundant code by combining with user-summary and user-music-from-list
     */
    @API("user-rating")
    suspend fun userRating(@RP username: Str) = us.cardByName(username) { card ->
        val extra = rp.userGeneralData.findByUser_Card_ExtId(card.extId)
            .associate { it.propertyKey to it.propertyValue }
        val best30Str = extra["rating_base_list"] ?: (400 - "No rating found")
        val recent10Str = extra["recent_rating_list"] ?: (400 - "No rating found")

        val best30 = best30Str.split(',').filterNot { it.isBlank() }.map { it.split(':') }
        val recent10 = recent10Str.split(',').filterNot { it.isBlank() }.map { it.split(':') }

        val musicIdList = listOf(
            best30.map { it[0].toInt() },
            recent10.map { it[0].toInt() },
        ).flatten()

        val userMusicList = rp.userMusicDetail.findByUser_Card_ExtIdAndMusicIdIn(card.extId, musicIdList)

        // Dont leak extId
        mapOf(
            "best30" to best30,
            "recent10" to recent10,
            "musicList" to userMusicList,
        )
    }

    // UserBox related APIs
    @API("user-box")
    fun userBox(@RP token: String) = us.jwt.auth(token) {
        val u = userDataRepo.findByCard(it.ghostCard) ?: (404 - "Game data not found")
        mapOf("user" to u, "items" to rp.userItem.findAllByUser(u))
    }

    private var teamRankingCacheLock = ReentrantLock()
    @PostConstruct
    fun teamRankingCacheInit() = thread { teamRankingCacheRun() }
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    fun teamRankingCacheRun() = teamRankingCacheLock.maybeLock { calculateTeamRanking() }

    // Chusan Teams Rankings
    private var teamRankingCache: List<ChusanTeamRanking> = emptyList()
    private var teamUserList = mutableMapOf<Long, MutableList<ChusanTeamUser>>()
    fun calculateTeamRanking() {
        // Update teams within SQL
        val time = millis()
        us.em.createNativeQuery(
            """
                UPDATE chusan_teams j
                JOIN (
                    SELECT
                        t.team_id,
                        t.name,
                        COUNT(p.team_id) AS members,
                        COALESCE(SUM(p.team_contribution), 0) AS contribution,
                        RANK() OVER (ORDER BY COALESCE(SUM(p.team_contribution), 0) DESC) AS ranking
                    FROM chusan_teams t
                    LEFT JOIN chusan_user_data p ON t.team_id = p.team_id
                    GROUP BY t.team_id, t.name
                ) r ON j.team_id = r.team_id
                SET j.ranking = r.ranking, 
                    j.points = r.contribution,
                    j.members_count = r.members;
            """
        ).exec
        // Read team rankings
        teamRankingCache = us.em.createNativeQuery(
            """
                SELECT
                    ranking,
                    name,
                    team_id,
                    members_count,
                    points
                FROM chusan_teams;
            """
        ).exec.mapIndexed{ i, it ->
            ChusanTeamRanking(
                rank = it[0]!!.int,
                name = it[1].toString(),
                id = it[2]!!.int,
                memberCount = it[3]!!.int,
                points = (it[4] ?: 0).int
            )
        }.sortedWith( compareBy{it.rank} )
        updateTeamMembers()
        logger.info("Chusan team rankings computed in ${millis() - time}ms")
    }

    fun updateTeamMembers() {
        // Read members of teams
        teamUserList = mutableMapOf<Long, MutableList<ChusanTeamUser>>()
        val allTeamedChusanUsers = us.em.createNativeQuery(
            """
                SELECT
                    t.team_id,
                    u.user_name,
                    u.team_contribution,
                    a.username
                FROM chusan_user_data u
                    JOIN chusan_teams t ON u.team_id = t.team_id
                    JOIN sega_card c ON u.card_id = c.id
                    LEFT JOIN aqua_net_user a ON c.net_user_id = a.au_id
                ORDER BY u.team_contribution;
            """
        ).exec
        // TODO: this can be made concise
        allTeamedChusanUsers.forEach { u ->
            val teamId = u[0]!!.long()
            if (!teamUserList.contains(teamId))
                teamUserList[teamId] = mutableListOf<ChusanTeamUser>()
            teamUserList[teamId]?.add(ChusanTeamUser(
                name = u[1].toString(),
                contribution = u[2]!!.int(),
                username = u[3].toString()
            ))
        }
    }

    @API("teams")
    fun teamsOverview(@RP token: String?): Map<String, Any?> {
        // TODO: move to scheduled
        val userData = token?.let { us.jwt.auth(it) }?.let {
            u -> userDataRepo.findByCard(u.ghostCard)
        };
        return mapOf(
            "teams" to teamRankingCache,
            "team" to userData?.teamId,
            "createStatus" to mapOf(
                "minimum" to props.teamLevelMinimum,
                "qualified" to if (props.teamLevelMinimum != null) {
                    props.teamLevelMinimum!! <= (userData?.level ?: 0)} else {true}
            ),
            "maximumMemberCount" to props.teamMaximum
        );
    }

    @API("team")
    fun teamInfo(@RP id: Long): Map<String, Any?> {
        return rp.teams.findTeam(id)?.let {
            t -> mapOf(
                "ranking" to t.ranking,
                "id" to t.id,
                "points" to t.points,
                "members" to teamUserList[t.id],
                "isFull" to (props.teamMaximum?.let{m -> m <= (teamUserList[t.id]?.size ?: 0)} ?: false),
                "owner" to t.ownerAuId?.let { us.userRepo.findByAuId(it.long()) }?.let{
                    u -> mapOf(
                        "username" to u.username,
                        "displayName" to u.publicFields["displayName"]
                    )
                }
            )
        } ?: mapOf("error" to true)
    }

    @API("team-requests")
    fun teamRequests(@RP token: String): Map<String, Any?> = us.jwt.auth(token) {
        return mapOf(
            "incoming" to (rp.teams.findTeamByOwnerAu(it.auId)?.let {
                t -> rp.teamRequests.findTeamRequest(t.id).map{
                    m -> mapOf(
                        "id" to m.id,
                        "date" to m.request_time,
                        "user" to m.request_au_id
                            .let { us.userRepo.findByAuId(it.long()) }?.let{
                                u -> mapOf(
                                    "username" to u.username,
                                    "displayName" to u.publicFields["displayName"]
                                )
                            }
                        )
                    }
                } ?: emptyList()),
            "outgoing" to rp.teamRequests.findRequestByRequesterId(it.auId)
        )
    }
    @API("team-request")
    fun manageTeamRequest(@RP token: String, @RP requestId: Long, @RP status: Boolean) = us.jwt.auth(token)  {
        val team = rp.teams.findTeamByOwnerAu(it.auId)
        if (team == null) (403 - "You do not manage a team");
        if (teamUserList.isEmpty()) (500 - "Please wait a moment");

        val request = rp.teamRequests.findById(requestId)
        if (!request.isPresent) (400 - "Unknown request");
        if (request.get().team_id != team.id) (403 - "Invalid request");

        val memberCount = teamUserList[team.id]?.size;
        if (status && !(props.teamMaximum?.let{m -> m <= (memberCount ?: 0)} ?: false)) {
            val userData = us.userRepo.findByAuId(request.get().request_au_id)?.let{
                user -> rp.userData.findByCard(user.ghostCard);
            };
            if (userData != null) {
                rp.userData.save(userData.apply{
                    teamId = team.id
                })
            }
        }
        rp.teamRequests.deleteById(requestId);
        updateTeamMembers();
        mapOf("status" to "ok")
    }
    @API("team-join")
    fun joinTeamRequest(@RP token: String, @RP teamId: Long?) : Any = us.jwt.auth(token) {
        val userData = us.userRepo.findByAuId(it.auId)?.let{
            user -> rp.userData.findByCard(user.ghostCard);
        };
        val owningTeam = rp.teams.findTeamByOwnerAu(it.auId);
        if (userData != null && userData.teamId > 0) {
            userData.teamId = 0;
            rp.userData.save(userData);

            // TODO: instead of deleting the entire team, we can probably make it pass the team down?
            //          it would be more like you're abandoning your team than killing everyone

            if (owningTeam != null) {
                us.em.createNativeQuery("UPDATE chusan_user_data WHERE team_id = :team_id SET team_id = 0, team_contribution = 0;")
                    .setParameter("team_id", owningTeam.id);
                rp.teams.deleteById(owningTeam.id);
            }
        }

        val outgoing = rp.teamRequests.findRequestByRequesterId(it.auId);
        if (outgoing != null)
            rp.teamRequests.deleteById(outgoing.id);

        val memberCount = teamUserList[teamId]?.size;
        if (props.teamMaximum?.let{m -> m <= (memberCount ?: 0)} ?: false) (403 - "This team is full")
        if (teamId != null)
            rp.teamRequests.save(
                Chu3TeamRequest().apply{
                    team_id = teamId
                    request_au_id = it.auId
                    request_time = LocalDateTime.now()
                }
            )
        mapOf("status" to "ok")
    }
    @API("create-team")
    fun createTeam(@RP token: String, @RP teamName: String) : Any = us.jwt.auth(token) {
        val userData = us.userRepo.findByAuId(it.auId)?.let{
            user -> rp.userData.findByCard(user.ghostCard);
        };
        if (userData == null) (400 - "No user data");
        if (props.teamLevelMinimum != null && userData.level < props.teamLevelMinimum!!)
            return mapOf("error" to "You must be level " + props.teamLevelMinimum!! + " to create a team");
        if (userData.teamId > 0) (400 - "You cannot create a team while in a team.");
        if (rp.teams.findTeamByName(teamName) != null) (400 - "A team with this name already exists.");

        rp.teams.save(
            Chu3Team().apply{
                name = teamName
                ownerAuId = it.auId
            }
        );
        rp.teams.findTeamByOwnerAu(it.auId)?.let{
            team -> userData.teamId = team.id
        };
        rp.userData.save(userData)
        mapOf("status" to "ok")
    }
}
