package co.orangesoft.phone

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.UpdateCredAuthResult
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneCredentialController(private val phoneAuthCredential: FirebaseAuthCredential.Phone): BaseFirebaseCredentialController(phoneAuthCredential) {

    private fun phoneSingInClient(activity: FragmentActivity) {
        val options = PhoneAuthOptions.newBuilder(authInstance)
            .setPhoneNumber(phoneAuthCredential.phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.i("!!!", "Code sent $verificationId")
                    phoneAuthCredential.onCodeSentListener?.invoke(verificationId, forceResendingToken)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.i("!!!", "Verification completed: ${credential.smsCode}")
                    emitAuthTask(credential)
                    getCredential()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.e("!!!", "Verification failed: ${p0.message}")
                    onError("Error phoneSingIn", p0)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    Log.e("!!!", "CodeAutoRetrievalTimeOut $p0")
                    super.onCodeAutoRetrievalTimeOut(p0)
                }
            })
        (phoneAuthCredential.forceResendingToken as? PhoneAuthProvider.ForceResendingToken)?.let {
            options.setForceResendingToken(it)
        }
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
        Log.i("!!!", "ClientId: ${phoneAuthCredential.phoneNumber}")
        if (phoneAuthCredential.verificationId == null) {
            Log.i("!!!", "Create phone provider")
            phoneSingInClient(activity)
        }
    }

    //TODO update deprecated method
    override fun updateCurrentCredential(user: FirebaseUser, authCredential: AuthCredential): Task<UpdateCredAuthResult> =
        user.updatePhoneNumber(authCredential as PhoneAuthCredential)
            .continueWithTask { Tasks.call { UpdateCredAuthResult(user, authCredential) } }

    override fun onActivityResult(code: Int, data: Intent?) {}

    override fun getCredential() {
        if (phoneAuthCredential.verificationId != null && phoneAuthCredential.code != null) {
            val credential = PhoneAuthProvider.getCredential(phoneAuthCredential.verificationId!!, phoneAuthCredential.code!!)
            emitAuthTask(credential)
        }
        super.getCredential()
    }

}