package icu.samnyan.aqua.net.db

import ext.*
import jakarta.persistence.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

enum class AdministrationLogType (val type: Int) {
    NONE(0),
    RANKINGBANSTATE(1),
    RESETUSERFIELD(2),
    PROMOTION(3),
    EXPORTDATA(4)
}

@Entity
@Table(name = "aqua_net_logs")
class AquaNetLog (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var type: AdministrationLogType = AdministrationLogType.NONE,
    var details: String?,
    var date: Instant = Instant.now(),

    @ManyToOne
    @JoinColumn(name = "auId", referencedColumnName = "auId")
    var aquaNetUser: AquaNetUser = AquaNetUser(),

    @ManyToOne
    @JoinColumn(name = "affectedAuId", referencedColumnName = "auId")
    var affectedAquaNetUser: AquaNetUser? = AquaNetUser()
)

@Repository
interface AquaNetLogRepo : JpaRepository<AquaNetLog, Long> {
    fun findByAquaNetUserAuId(auId: Long): List<AquaNetLog>
    fun findByAquaNetUserAuId(auId: Long, page: Pageable): Page<AquaNetLog>
}