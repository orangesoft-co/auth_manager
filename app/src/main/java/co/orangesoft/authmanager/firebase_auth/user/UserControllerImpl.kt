package co.orangesoft.authmanager.firebase_auth.user

import android.util.Log
import by.orangesoft.auth.firebase.FirebaseProfile
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.parseResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable

class UserControllerImpl(
    firebaseInstance: FirebaseAuth,
    private val profileService: ProfileService
) : FirebaseUserController(firebaseInstance), CoroutineScope {

    companion object {
        private const val TAG = "UserControllerImpl"
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override suspend fun saveChanges(onError: ((Throwable) -> Unit)?) {
        (profile as? Profile)?.let { profile ->
            profileService::patchProfile.parseResponse(accessToken, profile){
                onSuccess { super.updateAccount(it, onError) }
                onError(onError)
            }
        }
    }

    override suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)?) {
        (profile as? Profile)?.let { profile ->
            profileService::postProfileAvatar.parseResponse(accessToken, file.asRequestBody("image/*".toMediaTypeOrNull())){
                onSuccess { super.updateAvatar(file, onError) }
                onError(onError)
            }
        }
    }

    override suspend fun reload(onError: ((Throwable) -> Unit)?) {
        profileService::getProfile.parseResponse(accessToken){
            onSuccess { super.updateAccount(it, onError) }
            onError(onError)
        }
    }

    override suspend fun updateAccount(firebaseProfile: FirebaseProfile, onError: ((Throwable) -> Unit)?) {
        (firebaseProfile as? Profile)?.let { profile ->
            profileService::patchProfile.parseResponse(accessToken, profile){
                onSuccess { super.updateAccount(it, onError) }
                onError(onError)
            }
        }
    }
}