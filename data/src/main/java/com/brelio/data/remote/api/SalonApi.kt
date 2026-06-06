package com.brelio.data.remote.api

import com.brelio.data.remote.dto.SalonMemberDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SalonApi {

    @GET("rest/v1/salon_members")
    suspend fun getSalonMember(
        @Query("select") select: String = "salon_id,role,salons(user_id,timezone,slug,country)",
        @Query("user_id") userId: String,
        @Query("accepted_at") acceptedAt: String = "not.is.null",
    ): List<SalonMemberDto>
}
