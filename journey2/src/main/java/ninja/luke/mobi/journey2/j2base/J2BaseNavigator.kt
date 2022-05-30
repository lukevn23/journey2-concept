package ninja.luke.mobi.journey2.j2base

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import ninja.luke.mobi.journey2.contract.J2ContractNavigator
import ninja.luke.mobi.journey2.contract.J2ContractNavigator.*
import kotlin.coroutines.CoroutineContext

abstract class J2BaseNavigator(protected open val savedStateHandle: SavedStateHandle? = null) :
    ViewModel(), J2ContractNavigator {

    override var microArguments: Bundle? = null

    protected val _navigation = Channel<J2NavEvent>(Channel.RENDEZVOUS)
    override val navigation: ReceiveChannel<J2NavEvent> get() = _navigation

    override fun offerNavEvent(event: J2NavEvent) {
        _navigation.trySend(event)
    }

    override suspend fun sendNavEvent(event: J2NavEvent) {
        _navigation.send(event)
    }

    override fun viewModelScopeLaunch(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context, start, block)
    }

    //----------//----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- pre define events

}