package com.brelio.domain.repository

import com.brelio.domain.model.SalonContext

interface SalonRepository {
    suspend fun getSalonContext(userId: String): Result<SalonContext?>
}
