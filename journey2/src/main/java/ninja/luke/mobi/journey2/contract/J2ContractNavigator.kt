package ninja.luke.mobi.journey2.contract

import android.os.Bundle
import androidx.navigation.NavDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface J2ContractNavigator : J2Route {

    var microArguments: Bundle?

    val navigation: ReceiveChannel<J2NavEvent>
    fun offerNavEvent(event: J2NavEvent)
    suspend fun sendNavEvent(event: J2NavEvent)

    fun viewModelScopeLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    )

    open class J2NavEvent {
        open val action: Int = 0
        open val extras: Bundle? = null
    }

    interface J2NavListener {
        fun onNavEventReceived(event: J2NavEvent) = Unit
    }

    //----------//----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- events

    data class NextScreen(override val action: Int, override val extras: Bundle? = null) :
        J2ContractNavigator.J2NavEvent()

    data class PopScreen(override val action: Int = 0, override val extras: Bundle? = null) :
        J2ContractNavigator.J2NavEvent()

    object NotImplementedYet : J2ContractNavigator.J2NavEvent()
}