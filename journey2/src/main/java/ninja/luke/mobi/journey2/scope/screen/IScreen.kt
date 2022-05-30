package ninja.luke.mobi.journey2.scope.screen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import ninja.luke.mobi.journey2.BuildConfig
import ninja.luke.mobi.journey2.Constant
import ninja.luke.mobi.journey2.contract.J2ContractFragment
import ninja.luke.mobi.journey2.j2base.J2BaseNavigator
import ninja.luke.mobi.journey2.contract.J2Route
import ninja.luke.mobi.journey2.j2base.J2BaseMicro
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.qualifier.qualifier
import java.lang.Exception
import java.lang.RuntimeException

interface J2ScreenRoute : J2Route

interface IScreen<Route : J2ScreenRoute> : J2BaseMicro<Route>, J2ContractFragment {

    //override val sdk: J2BaseSdk<*>
    override val navigator: J2BaseNavigator
    override val route: Route? get() = castToRouteOrCrash()
    override val microArguments: Bundle? get() = extractJourneyArguments()

    override fun afterOnViewCreated() = checkAndWarningRouteIsNotImplementation()

}

//----------//----------//---------- implementation route
//region route
private fun IScreen<*>.debugRoute(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "screenRoute:   $message", e)
        return
    }
    if (BuildConfig.DEBUG) {
        Log.d(Constant.TAG, "screenRoute:   $message")
    }
}

@Suppress("UNCHECKED_CAST")
private fun <Route : J2ScreenRoute> IScreen<Route>.castToRouteOrCrash(): Route? {
    try {
        val lazyNavigator = navigator
        (lazyNavigator as? Route)?.let { simpleRoute ->
            return simpleRoute
        }
        val message = "${lazyNavigator::class.java.simpleName} does NOT YET implement ${this::class.java.simpleName}'s Route. It's mandatory for routing screens."
        debugRoute(message)
        return null
        //throw RuntimeException(message)
    } catch (e: Exception) {
        val message = "Exception while accessing navigator"
        debugRoute(message, e)
        return null
    }
}

@Suppress("UNCHECKED_CAST")
private fun <Route : J2ScreenRoute> IScreen<Route>.checkAndWarningRouteIsNotImplementation() {
    if (BuildConfig.DEBUG) {
        val simpleNavigator = navigator
        if ((simpleNavigator as? Route) == null) {
            Log.e(
                Constant.TAG,
                "${this::class.java.simpleName}.onViewCreated:   ${simpleNavigator::class.java.simpleName} have NOT YET IMPLEMENT this screen route"
            )
        }
    }
}
//endregion

//----------//----------//---------- implementation extras
//region extras
private fun IScreen<*>.extractJourneyArguments(): Bundle? {
    navigator.microArguments?.let {
        return Bundle().apply {
            this.putAll(it)
        }
    }
    return null
}
//endregion


//----------//----------//---------- implementation view model
//region ViewModel
private fun IScreen<*>.debugViewModel(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "screenVM:   $message", e)
        return
    }
    if (BuildConfig.DEBUG) {
        Log.d(Constant.TAG, "screenVM:   $message")
    }
}

fun IScreen<*>.journeyFragmentOrCrash(): Fragment {
    if (this is Fragment) {
        parentFragment?.parentFragment?.let {
            debugViewModel("asFragmentOrCrash")
            return it
        }
        //this may lead to create multiple instance of viewmodel/route,
        //return this
    }
    throw RuntimeException("Screen ${this::class.java.simpleName} is not a fragment")
}

fun IScreen<*>.getSharedViewModelStoreOwner(): ViewModelStoreOwner {
    if (this is Fragment) {
        parentFragment?.parentFragment?.let {
            debugViewModel("getSharedViewModelStoreOwner")
            return it
        }
        return requireActivity()
    }
    throw RuntimeException("Screen ${this::class.java.simpleName} is not a fragment")
}

fun IScreen<*>.getSharedSavedStateRegistryOwner(): SavedStateRegistryOwner {
    if (this is Fragment) {
        parentFragment?.parentFragment?.let {
            debugViewModel("getSharedSavedStateRegistryOwner")
            return it
        }
        return requireActivity()
    }
    throw RuntimeException("Screen ${this::class.java.simpleName} is not a fragment")
}

fun IScreen<*>.injectJourneyNavigator(): Lazy<J2BaseNavigator> {
    return lazy(LazyThreadSafetyMode.NONE) {
        debugViewModel("injectJourneyNavigator")
        getSharedViewModelStoreOwner().getViewModel(
            clazz = J2BaseNavigator::class,
            qualifier = qualifier(sdk.journeyName)
        )  //use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getViewModel(qualifier = qualifier(sdk.journeyName))//todo try for 3.1.6
    }
}

fun IScreen<*>.injectJourneyNavigatorState(): Lazy<J2BaseNavigator> {
    return lazy(LazyThreadSafetyMode.NONE) {
        debugViewModel("injectJourneyNavigatorState")
        getSharedSavedStateRegistryOwner().getStateViewModel(
            clazz = J2BaseNavigator::class,
            qualifier = qualifier(sdk.journeyName)
        )//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getStateViewModel(qualifier = qualifier(sdk.journeyName))//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> IScreen<*>.injectJourneyViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectJourneyViewModel")
        getSharedViewModelStoreOwner().getViewModel()//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> IScreen<*>.injectJourneyViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectJourneyViewModelState")
        getSharedSavedStateRegistryOwner().getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getStateViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> IScreen<*>.injectScreenViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectScreenViewModel")
        if (this is Fragment) {
            this.getViewModel()//use this way for both 2.2.1 and 2.1.6
//            this.getViewModel()//todo try for 3.1.6
        } else {
            throw RuntimeException("Screen ${this::class.java.simpleName} is not a fragment")
        }
    }
}

inline fun <reified T : ViewModel> IScreen<*>.injectScreenViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectScreenViewModelState")
        if (this is Fragment) {
            this.getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//            this.getStateViewModel()//todo try for 3.1.6
        } else {
            throw RuntimeException("Screen ${this::class.java.simpleName} is not a fragment")
        }
    }
}

//endregion
