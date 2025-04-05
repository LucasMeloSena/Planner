package br.dev.lucasena.planner.data.datasource

import br.dev.lucasena.planner.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface UserRegistrationLocalDataSource {
    val profile: Flow<Profile>

    fun getIsUserRegistered(): Boolean
    fun saveIsUserRegistered(value: Boolean)

    suspend fun saveProfile(profile: Profile)
}