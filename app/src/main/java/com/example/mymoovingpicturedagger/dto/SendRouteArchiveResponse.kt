package com.example.mymoovingpicturedagger.dto

import com.example.mymoovingpicturedagger.domain.SendRouteDomain
import com.google.gson.annotations.SerializedName

data class SendRouteArchiveResponse(
    @SerializedName("recordNumber") val recordNumber: Long?,
    @SerializedName("recordName") val recordName: String?
)

fun asDomainnnSendRouteArchiveResponseModel(lll: List<SendRouteArchiveResponse>): List<SendRouteDomain> {
    val answerlist = mutableListOf<SendRouteDomain>()
    lll.forEach { sendRouteDomain ->
        answerlist.add(

            SendRouteDomain(
                recordName = sendRouteDomain.recordName ?: "",
                recordNumber = sendRouteDomain.recordNumber ?: 0L
            )
        )
    }
    return answerlist
}
