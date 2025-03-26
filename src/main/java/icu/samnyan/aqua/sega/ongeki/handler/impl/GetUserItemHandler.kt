package icu.samnyan.aqua.sega.ongeki.handler.impl

import ext.*
import icu.samnyan.aqua.sega.general.BaseHandler
import icu.samnyan.aqua.sega.ongeki.OgkUserDataRepo
import icu.samnyan.aqua.sega.ongeki.OgkUserItemRepo
import icu.samnyan.aqua.sega.ongeki.model.common.ItemType
import icu.samnyan.aqua.sega.ongeki.model.UserItem
import icu.samnyan.aqua.sega.util.jackson.BasicMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component("OngekiGetUserItemHandler")
class GetUserItemHandler(
    val mapper: BasicMapper,
    val userDataRepo: OgkUserDataRepo,
    val userItemRepo: OgkUserItemRepo
) : BaseHandler {
    val log = logger()

    override fun handle(request: Map<String, Any>): String {
        val uid = parsing { request["userId"]!!.long }
        val nextIndexVal = parsing { request["nextIndex"]!!.long }
        val maxCount = parsing { request["maxCount"]!!.int }

        val mul = 10000000000L
        val kind = (nextIndexVal / mul).toInt()
        val pg = (nextIndexVal % mul).toInt() / maxCount

        var dat = userItemRepo.findByUser_Card_ExtIdAndItemKind(uid, kind, PageRequest.of(pg, maxCount)).content

        // Check if user have infinite kaika
        if (kind == ItemType.KaikaItem.ordinal) {
            val u = userDataRepo.findByCard_ExtId(uid)()
            u?.card?.aquaUser?.gameOptions?.let {
                if (it.ongekiInfiniteKaika) {
                    dat = listOf(UserItem().apply {
                        user = u
                        itemKind = ItemType.KaikaItem.ordinal
                        itemId = 1
                        stock = 999
                    })
                }
            }
        }

        val resultMap = mapOf(
            "userId" to uid,
            "length" to dat.size,
            "nextIndex" to if (dat.size < maxCount) -1 else kind * mul + maxCount * pg + dat.size,
            "itemKind" to kind,
            "userItemList" to dat
        )

        return mapper.write(resultMap).also { log.info("Response: $it") }
    }
}
