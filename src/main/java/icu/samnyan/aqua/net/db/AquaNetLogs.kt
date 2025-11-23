package icu.samnyan.aqua.net.db

import jakarta.persistence.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "aqua_net_logs")
class AquaNetLog (
    @Column(nullable = false)
    var type: Int = 0,
    var details: String,
    var date: Instant = Instant.now(),

    @ManyToOne
    @JoinColumn(name = "auId", referencedColumnName = "auId")
    var aquaNetUser: AquaNetUser = AquaNetUser(),

    @ManyToOne
    @JoinColumn(name = "affectedAuId", referencedColumnName = "affectedAuId")
    var affectedAquaNetUser: AquaNetUser = AquaNetUser()
)

@Repository
interface AquaNetLogRepo : JpaRepository<AquaNetLog, Long> {
    fun findByAquaNetUserAuId(auId: Long): List<AquaNetLog>
    fun findByAquaNetUserAuId(auId: Long, page: Pageable): Page<AquaNetLog>
}