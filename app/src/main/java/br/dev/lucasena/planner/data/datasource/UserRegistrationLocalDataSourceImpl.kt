package br.dev.lucasena.planner.data.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import br.dev.lucasena.planner.domain.model.Profile
import br.dev.lucasena.planner.domain.model.ProfileSerializer
import kotlinx.coroutines.flow.Flow

private const val USER_REGISTRATION_FILE_NAME = "user_registration"
private const val IS_USER_REGISTERED = "is_user_registered"
private const val PROFILE_FILE_NAME = "profile.db"

class UserRegistrationLocalDataSourceImpl(
    private val applicationContext: Context
): UserRegistrationLocalDataSource {

    private val Context.profileProtoDataStore: DataStore<Profile> by dataStore(
        fileName = PROFILE_FILE_NAME,
        serializer = ProfileSerializer
    )

    private val userRegistrationSharedPreferences: SharedPreferences = applicationContext.getSharedPreferences(
        USER_REGISTRATION_FILE_NAME, Context.MODE_PRIVATE
    )

    override val profile: Flow<Profile>
        get() = applicationContext.profileProtoDataStore.data

    override fun getIsUserRegistered(): Boolean {
        return userRegistrationSharedPreferences.getBoolean(IS_USER_REGISTERED, false)
    }

    override fun saveIsUserRegistered(value: Boolean) {
        with (userRegistrationSharedPreferences.edit()) {
            putBoolean(IS_USER_REGISTERED, value)
            apply()
        }
    }

    override suspend fun saveProfile(profile: Profile) {
        applicationContext.profileProtoDataStore.updateData { profile }
    }

}