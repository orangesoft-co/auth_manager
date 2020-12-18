package by.orangesoft.auth.credentials

import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

interface IBaseCredentialController {

    val credential: AuthCredential

    fun addCredential(listener: CredentialListener.() -> Unit) = addCredential(CredentialListener().apply(listener))
    fun addCredential(listener: CredentialListener)

    fun removeCredential(listener: CredentialListener.() -> Unit) = removeCredential(CredentialListener().apply(listener))
    fun removeCredential(listener: CredentialListener)

    fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>)

    fun onActivityResult(code: Int, data: Intent?)

    fun setActivity(activity: FragmentActivity) {
        if(activity is ComponentCallbackActivity)
            activity.setActivityResultCallback(ActivityResultCallback { onActivityResult(it.resultCode, it.data) })

        val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> onActivityResult(result.resultCode, result.data) }
        onProviderCreated(activity, launcher)
    }
}