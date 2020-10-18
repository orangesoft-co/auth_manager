package co.orangesoft.authmanager.api.request

import androidx.annotation.Keep
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.moshi.Json

@Keep
data class LoginRequest(
    val platform: String = "android"
) {
    @Json(name = "push_token")
    var pushToken: String? = null

    fun waitToken(listener: (LoginRequest) -> Unit) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { result ->
            result.exception?.printStackTrace()
            pushToken = result.result?.token
            listener(this)
        }
    }
}