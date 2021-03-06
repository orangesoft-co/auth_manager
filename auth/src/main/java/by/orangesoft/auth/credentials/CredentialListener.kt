package by.orangesoft.auth.credentials

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CredentialListener constructor(override val coroutineContext: CoroutineContext = Dispatchers.IO): CoroutineScope {

    constructor(unit: CredentialListener.()-> Unit):this() {
        apply(unit)
    }

    private var onAddSuccess: ((CredentialResult) -> Unit)? = null
    private var onRemoveSuccess: ((BaseAuthCredential) -> Unit)? = null
    private var onException: ((Throwable) -> Unit)? = null

    fun onAddCredentialSuccess(listener: (CredentialResult) -> Unit) {
        onAddSuccess = listener
    }

    fun onRemoveCredentialSuccess(listener: (BaseAuthCredential) -> Unit) {
        onRemoveSuccess = listener
    }

    fun onCredentialException(listener: (Throwable) -> Unit) {
        onException = listener
    }


    operator fun invoke(result: CredentialResult) {
        launch {
            synchronized(this) {
                onAddSuccess?.invoke(result)
            }
        }
    }

    operator fun invoke(result: BaseAuthCredential) {
        launch {
            synchronized(this) {
                onRemoveSuccess?.invoke(result)
            }
        }
    }

    operator fun invoke(result: Throwable) {
        launch {
            synchronized(this) {
                onException?.invoke(result)
            }
        }
    }
}