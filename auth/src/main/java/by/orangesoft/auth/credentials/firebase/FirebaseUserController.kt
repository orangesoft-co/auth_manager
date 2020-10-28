package by.orangesoft.auth.credentials.firebase

import android.util.Log
import by.orangesoft.auth.user.BaseUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.runBlocking
import java.io.File

abstract class FirebaseUserController<T>(protected val firebaseInstance: FirebaseAuth)
    : BaseUserController<T> {

    abstract override var profile: T?

    val currentUser: FirebaseUser? = firebaseInstance.currentUser

    override suspend fun update() {
        currentUser?.let {
            firebaseInstance.updateCurrentUser(it)
        }
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        //do nothing
    }

    override suspend fun refresh() {
        currentUser?.reload()
    }

    override suspend fun getAccessToken(): String {
        var token = ""
        runBlocking {
            firebaseInstance.currentUser?.getIdToken(true)?.addOnCompleteListener {
                if  (it.isSuccessful) {
                    token = it.result?.token ?: ""
                } else {
                    Log.e("FirebaseUserController", "Cannot get access token")
                }
            }
        }

        return token
    }

    override fun updateAccount(function: (UserProfileChangeRequest.Builder) -> Unit) {
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder().also {
                function.invoke(it)
            }.build()).addOnSuccessListener {
                firebaseInstance.updateCurrentUser(this)
            }
        }
    }
}
