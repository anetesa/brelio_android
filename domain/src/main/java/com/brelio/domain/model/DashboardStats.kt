package com.brelio.domain.model

data class DashboardStats(
    val todayAppointments: Int,
    val todayRevenue: Double,
    val totalClients: Int,
    val noShowCount: Int,
)
