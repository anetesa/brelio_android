package com.brelio.data.remote.api

import com.brelio.data.remote.dto.AppointmentDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentApi {

    @GET("rest/v1/appointments")
    suspend fun getAppointments(
        @Query("select") select: String = "id,client_id,stylist_id,start_at,end_at,status,notes,payment_status,payment_method,clients(name),stylists(name),appointment_services(id,service_name,duration_min,price)",
        @Query("user_id") userId: String,
        @Query("start_at") startAtGte: String? = null,
        @Query("end_at") endAtLte: String? = null,
        @Query("order") order: String = "start_at.asc",
    ): List<AppointmentDto>
}
