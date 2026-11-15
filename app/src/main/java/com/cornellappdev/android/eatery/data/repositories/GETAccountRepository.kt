package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.BarcodeCredentials
import com.cornellappdev.android.eatery.data.models.LoginPIN
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.Result
import com.cornellappdev.android.eatery.data.models.SessionID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GETAccountRepository @Inject constructor(
    private val networkApi: NetworkApi,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun linkGETAccount(sessionId: String): Result<Unit> {
        // The new session may belong to a different GET account, so never reuse its barcode.
        clearBarcodeData()
        userPreferencesRepository.setSessionId(sessionId)
        val pin = Random.nextInt(10000)
        userPreferencesRepository.setPin(pin)
        return resultOfNetworkCall {
            networkApi.authorizeUser(
                loginRequest = LoginRequest(pin.toString(), sessionId)
            )
        }.also { result ->
            if (result is Result.Success) {
                setIsLoggedIn(true)
            }
        }
    }

    suspend fun refreshLogin(pin: Int): Result<Unit> = resultOfNetworkCall {
        val newSessionId = networkApi.refreshAuthorizedUser(
            loginPIN = LoginPIN(pin.toString())
        ).sessionId ?: throw IllegalStateException("Session ID is null")
        userPreferencesRepository.setSessionId(newSessionId)
    }

    /**
     * Fetches the barcode seed and patron ID and caches them for offline barcode generation.
     * Refreshes the GET session and retries once if the first request fails.
     *
     * Only real values from the backend are stored: a missing or blank field leaves the cached
     * value as it was rather than being replaced with a placeholder. The patron ID is missing
     * when the account has no transactions yet.
     *
     * @return the cached credentials after the update, or null if either value is still missing
     */
    suspend fun refreshBarcodeData(): Result<BarcodeCredentials?> = resultOfNetworkCall {
        val response = try {
            networkApi.getBarcode(SessionID(getSessionId()))
        } catch (ce: CancellationException) {
            throw ce
        } catch (_: Exception) {
            val pin = getPin() ?: throw IllegalStateException("PIN not available")
            val refreshResult = refreshLogin(pin = pin)
            if (refreshResult is Result.Error) throw IllegalStateException("refreshLogin failed")
            networkApi.getBarcode(SessionID(getSessionId()))
        }

        userPreferencesRepository.setBarcodeData(
            barcodeSeed = response.barcodeSeed?.takeIf { it.isNotBlank() },
            patronId = response.patronId?.takeIf { it.isNotBlank() }
        )
        getCachedBarcodeCredentials()
    }

    /**
     * Returns the cached barcode credentials without a network call, so it works offline.
     * Null if they haven't been fetched for the current GET account.
     */
    suspend fun getCachedBarcodeCredentials(): BarcodeCredentials? =
        userPreferencesRepository.barcodeCredentialsFlow.firstOrNull()

    suspend fun clearBarcodeData() = userPreferencesRepository.clearBarcodeData()

    suspend fun getSessionId(): String? = userPreferencesRepository.sessionIdFlow.firstOrNull()

    suspend fun getPin(): Int? = userPreferencesRepository.pinFlow.firstOrNull()

    suspend fun clearSessionId() = userPreferencesRepository.setSessionId("")

    suspend fun setIsLoggedIn(isLoggedIn: Boolean) =
        userPreferencesRepository.setIsLoggedIn(isLoggedIn)

    suspend fun isLoggedIn(): Boolean =
        userPreferencesRepository.isLoggedInFlow.firstOrNull() ?: false

}
