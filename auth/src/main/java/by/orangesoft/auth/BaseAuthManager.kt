package by.orangesoft.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import kotlinx.coroutines.Dispatchers

abstract class BaseAuthManager<T: FirebaseUserController<*>, C: Any>(protected val credentialsManager: BaseCredentialsManager<T, C>): AuthManagerInterface<T, C> {

    override val currentUser: LiveData<T> = MutableLiveData()

    private var authListener:  AuthListener<T>? = null

    protected open val onAuthSuccessListener: (T) -> Unit = {
            (currentUser as MutableLiveData).postValue(it)

            synchronized(this@BaseAuthManager) {
                authListener?.invoke(it)
                authListener = null
            }
        }

    protected open val onAuthErrorListener: (Throwable) -> Unit = {
        synchronized(this@BaseAuthManager) {
            authListener?.invoke(it)
            authListener = null
        }
    }

    private val credentialListener: AuthListener<T> = AuthListener(Dispatchers.IO) {
        onAuthSuccess(onAuthSuccessListener)
        onAuthException(onAuthErrorListener)
    }

    init {
        credentialsManager.setAuthListener(credentialListener)
        (currentUser as MutableLiveData).postValue(credentialsManager.getLoggedUser())
    }

    override fun login(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>?) {
        authListener = listener
        credentialsManager.login(activity, method)
    }

    override suspend fun logout(listener: AuthListener<T>?) {
        authListener = listener
        currentUser.value?.let { credentialsManager.logout(it) }
    }

    override suspend fun deleteUser(listener: AuthListener<T>?) {
        authListener = listener
        currentUser.value?.let { credentialsManager.deleteUser(it) }
    }

    override fun getCredentials(): LiveData<Set<C>> {
        return credentialsManager.credentials
    }

    override fun addCredential(activity: FragmentActivity, method: AuthMethod, listener: AuthListener<T>?) {
        authListener = listener
        currentUser.value?.let { credentialsManager.addCredential(activity, it, method) }
    }

    override fun removeCredential(credential: C, listener: AuthListener<T>?) {
        authListener = listener
        currentUser.value?.let { credentialsManager.removeCredential(it, credential) }
    }
}
