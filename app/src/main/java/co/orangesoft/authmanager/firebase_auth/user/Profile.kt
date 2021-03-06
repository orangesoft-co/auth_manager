package co.orangesoft.authmanager.firebase_auth.user

import by.orangesoft.auth.firebase.FirebaseProfile
import com.google.gson.annotations.SerializedName

class Profile(
    var id: String,
    providerId: String? = null,
    var name: String? = null,
    phoneNumber: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    var birthday: String? = null,
    var country: String? = null,
    var city: String? = null,
    var settings: Settings? = null
) : FirebaseProfile(uid = id, providerId = providerId, displayName = name, phoneNumber = phoneNumber, photoUrl = avatarUrl)


data class Settings(
    var customSetting1: String? = null,
    var customSetting2: String? = null
)