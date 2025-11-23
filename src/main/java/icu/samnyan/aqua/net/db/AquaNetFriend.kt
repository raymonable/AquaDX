package icu.samnyan.aqua.net.db

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
class AquaNetFriend(
    @JsonIgnore
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var date: Instant = Instant.now(),

    @ManyToOne
    @JoinColumn(name = "auId", referencedColumnName = "auId")
    var aquaNetUser: AquaNetUser = AquaNetUser(),

    @ManyToOne
    @JoinColumn(name = "friendAuId", referencedColumnName = "auId")
    var friendAquaNetUser: AquaNetUser? = AquaNetUser()
)

@Repository
interface AquaNetFriendRepo : JpaRepository<AquaNetFriend, Long> {
    fun findByAquaNetUserAuId(auId: Long): List<AquaNetFriend>
    fun findByAquaNetUserAuIdAndFriendAquaNetUserAuId(auId: Long, friendAuId: Long): AquaNetFriend?
}