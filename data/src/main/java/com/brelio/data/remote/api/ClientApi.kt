package com.brelio.data.remote.api

import com.brelio.data.remote.dto.ClientDto
import com.brelio.data.remote.dto.CreateClientRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ClientApi {

    @GET("rest/v1/clients")
    suspend fun getClients(
        @Query("select") select: String = "*",
        @Query("owner_id") ownerId: String,
        @Query("order") order: String = "name.asc",
    ): List<ClientDto>

    @GET("rest/v1/clients")
    suspend fun searchClients(
        @Query("select") select: String = "*",
        @Query("owner_id") ownerId: String,
        @Query("name") name: String,
        @Query("order") order: String = "name.asc",
    ): List<ClientDto>

    @GET("rest/v1/clients")
    suspend fun getClient(
        @Query("select") select: String = "*",
        @Query("id") id: String,
    ): List<ClientDto>

    @POST("rest/v1/clients")
    @Headers("Prefer: return=representation")
    suspend fun createClient(@Body body: CreateClientRequest): List<ClientDto>

    @PATCH("rest/v1/clients")
    @Headers("Prefer: return=representation")
    suspend fun updateClient(
        @Query("id") id: String,
        @Body body: ClientDto,
    ): List<ClientDto>

    @DELETE("rest/v1/clients")
    suspend fun deleteClient(@Query("id") id: String)
}
