package icu.samnyan.aqua.net

import ext.*
import icu.samnyan.aqua.net.db.AquaNetLog
import icu.samnyan.aqua.net.db.AquaNetLogRepo
import icu.samnyan.aqua.net.db.AquaNetUser
import icu.samnyan.aqua.net.db.AdministrationLogType
import icu.samnyan.aqua.net.db.AquaNetUserPermission
import icu.samnyan.aqua.net.db.AquaUserServices
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.RestController

@RestController
@API("api/v2/admin")
class Administration (
    val us: AquaUserServices,
    val logs: AquaNetLogRepo
) {
    val resettableFields: Map<String, (AquaNetUser) -> Unit> by lazy { mapOf(
        "username" to { u -> u.username = us.checkUsername("aqua${u.auId}") },
        "profilePfp" to { u -> u.profilePicture = null },
        "profileBio" to { u -> u.profileBio = null },
        "displayName" to { u -> u.displayName = "" }
    ) }

    @API("/logs")
    fun getLogs(@RP token: Str): Any = us.jwt.auth(token) { u ->
        if (u.permission > AquaNetUserPermission.USER) {
            logs.findAll(PageRequest.of(0, 50)).content
                .sortedByDescending { it.date }.map {
                    mapOf( "type" to it.type, "details" to it.details, "date" to it.date, "actioner" to it.aquaNetUser.publicFields, "affected" to it.affectedAquaNetUser?.publicFields )
                }
        } else 403 - "Unauthorized"
    }

    @API("/ranking-ban")
    @Doc("Bans a user from rankings. Moderators and higher.")
    suspend fun rankingBan(@RP username: Str, @RP token: Str, @RP ban: Bool): Any = us.cardByName(username) { card ->
        val actor = us.jwt.auth(token);
        val user = us.userRepo.findByGhostCardExtId(card.extId)
        if (user?.username != actor.username && actor.permission > AquaNetUserPermission.USER && actor.permission > (user?.permission ?: AquaNetUserPermission.USER)) {
            logs.save(AquaNetLog(
                type = AdministrationLogType.RANKINGBANSTATE,
                aquaNetUser = actor, affectedAquaNetUser = user,
                details = username
            ))
            us.cardRepo.save(card.apply {
                rankingBanned = ban
            })
            200 - "Success"
        } else
            403 - "Unauthorized"
    }

    @API("/promote")
    @Doc("Promotes a user to a certain permission. For administrators.")
    suspend fun promote(@RP username: Str, @RP token: Str, @RP role: Int): Any = us.byName(username) { user ->
        val actor = us.jwt.auth(token);
        if (user.username != actor.username && actor.permission > AquaNetUserPermission.MODERATOR && actor.permission > user.permission && role < actor.permission.int) {
            logs.save(AquaNetLog(
                type = AdministrationLogType.PROMOTION,
                aquaNetUser = actor, affectedAquaNetUser = user,
                details = role.coerceIn(0, 2).str
            ))
            us.userRepo.save(user.apply {
                permission = AquaNetUserPermission.entries[role.coerceIn(0, 2)]
            })
            200 - "Success"
        } else
            403 - "Unauthorized"
    }

    @API("/reset-field")
    @Doc("Resets a field on a user's profile. For moderators and above.")
    suspend fun resetField(@RP username: Str, @RP token: Str, @RP field: Str): Any = us.byName(username) { user ->
        if (resettableFields.containsKey(field)) {
            val actor = us.jwt.auth(token);
            if (actor.username != user.username && actor.permission > AquaNetUserPermission.USER && actor.permission > user.permission) {
                resettableFields[field]?.let { it(user) }
                logs.save(AquaNetLog(
                    type = AdministrationLogType.RESETUSERFIELD,
                    aquaNetUser = actor, affectedAquaNetUser = user,
                    details = field
                ))
                us.userRepo.save(user)
                200 - "Success"
            } else
                403 - "Unauthorized"
        } else
            400 - "Invalid field"
    }
}