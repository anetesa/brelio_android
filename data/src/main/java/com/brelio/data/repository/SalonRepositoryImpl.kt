package com.brelio.data.repository

import com.brelio.data.remote.api.SalonApi
import com.brelio.domain.model.SalonContext
import com.brelio.domain.model.SalonRole
import com.brelio.domain.repository.SalonRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalonRepositoryImpl @Inject constructor(
    private val salonApi: SalonApi,
) : SalonRepository {

    override suspend fun getSalonContext(userId: String): Result<SalonContext?> {
        return runCatching {
            val members = salonApi.getSalonMember(userId = "eq.$userId")
            val member = members.firstOrNull() ?: return@runCatching null
            val salon = member.salons ?: return@runCatching null
            SalonContext(
                salonId = member.salonId,
                ownerUserId = salon.userId,
                timezone = salon.timezone.orEmpty(),
                role = parseRole(member.role),
                salonSlug = salon.slug,
                country = salon.country.orEmpty(),
            )
        }
    }

    private fun parseRole(role: String): SalonRole {
        return when (role.lowercase()) {
            "owner" -> SalonRole.Owner
            "manager" -> SalonRole.Manager
            else -> SalonRole.Stylist
        }
    }
}
