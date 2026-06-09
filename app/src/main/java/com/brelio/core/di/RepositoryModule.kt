package com.brelio.core.di

import com.brelio.data.repository.AppointmentRepositoryImpl
import com.brelio.data.repository.AuthRepositoryImpl
import com.brelio.data.repository.ClientRepositoryImpl
import com.brelio.data.repository.SalonRepositoryImpl
import com.brelio.domain.repository.AppointmentRepository
import com.brelio.domain.repository.AuthRepository
import com.brelio.domain.repository.ClientRepository
import com.brelio.domain.repository.SalonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSalonRepository(impl: SalonRepositoryImpl): SalonRepository

    @Binds
    @Singleton
    abstract fun bindAppointmentRepository(impl: AppointmentRepositoryImpl): AppointmentRepository

    @Binds
    @Singleton
    abstract fun bindClientRepository(impl: ClientRepositoryImpl): ClientRepository
}
