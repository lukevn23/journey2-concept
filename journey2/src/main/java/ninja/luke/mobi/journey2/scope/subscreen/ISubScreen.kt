package ninja.luke.mobi.journey2.scope.subscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.NavHostFragment
import androidx.savedstate.SavedStateRegistryOwner
import ninja.luke.mobi.journey2.BuildConfig
import ninja.luke.mobi.journey2.Constant
import ninja.luke.mobi.journey2.contract.J2ContractFragment
import ninja.luke.mobi.journey2.j2base.J2BaseNavigator
import ninja.luke.mobi.journey2.contract.J2Route
import ninja.luke.mobi.journey2.j2base.J2BaseMicro
import ninja.luke.mobi.journey2.scope.journey.IJourney
import ninja.luke.mobi.journey2.scope.screen.IScreen
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.qualifier.qualifier
import java.lang.Exception
import java.lang.RuntimeException

interface J2SubScreenRoute : J2Route

interface ISubScreen<Route : J2SubScreenRoute> : J2BaseMicro<Route>, J2ContractFragment {

    //override val sdk: J2BaseSdk<*>
    override val navigator: J2BaseNavigator
    override val route: Route? get() = searchForRouteInParent()
    override val microArguments: Bundle? get() = extractJourneyArguments()

}


//----------//----------//---------- implementation route
//region route
private fun ISubScreen<*>.debugRoute(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "subScreenRoute:   $message", e)
        return
    }
    if (BuildConfig.DEBUG) {
        Log.d(Constant.TAG, "subScreenRoute:   $message")
    }
}

@Suppress("UNCHECKED_CAST")
private fun <Route : J2SubScreenRoute> ISubScreen<Route>.searchForRouteInParent(): Route? {
    if (this is Fragment) {
        var checkThisFragment: Fragment? = this
        do {
            val parent = checkThisFragment?.parentFragment
            (parent as? Route)?.let {
                return it
            }
            checkThisFragment = parent
        } while (checkThisFragment != null && checkThisFragment !is IJourney<*> && checkThisFragment !is NavHostFragment)
    }
    try {
        val lazyNavigator = navigator
        (lazyNavigator as? Route)?.let { simpleRoute ->
            return simpleRoute
        }
        val message =
            "${lazyNavigator::class.java.simpleName} does NOT YET implement ${this::class.java.simpleName}'s Route. It's mandatory for routing screens."
        debugRoute(message)
        return null
        //throw RuntimeException(message)
    } catch (e: Exception) {
        val message = "Exception while accessing navigator"
        debugRoute(message, e)
        return null
    }
}
//endregion

//----------//----------//---------- implementation extras
//region extras
private fun ISubScreen<*>.extractJourneyArguments(): Bundle? {
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
private fun ISubScreen<*>.debugViewModel(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "screenVM:   $message", e)
        return
    }
}

//----------//----------//---------- journey view model

private fun ISubScreen<*>.getJourneyFragment(): Fragment? {
    if (this is Fragment) {
        //search for iJourney
        var checkThisFragment: Fragment? = this
        do {
            val parent = checkThisFragment?.parentFragment
            (parent as? IJourney<*>)?.let {
                if (it is Fragment) {
                    return it
                }
            }
            checkThisFragment = parent
        } while (checkThisFragment != null && checkThisFragment !is IJourney<*>)
    }
    return null
}

fun ISubScreen<*>.journeyFragmentOrCrash(): Fragment {
    getJourneyFragment()?.let {
        return it
    }
    throw RuntimeException("SubScreen ${this::class.java.simpleName} is not a fragment")
}

fun ISubScreen<*>.getSharedViewModelStoreOwner(): ViewModelStoreOwner {
    getJourneyFragment()?.let {
        debugViewModel("getSharedViewModelStoreOwner")
        return it
    }
    if (this is Fragment) {
        Log.e(
            Constant.TAG,
            "getSharedViewModelStoreOwner:   SubScreen ${this::class.java.simpleName} is NOT inside a journey"
        )
        return requireActivity()
    }
    throw RuntimeException("getSharedViewModelStoreOwner: SubScreen ${this::class.java.simpleName} is not a fragment")
}

fun ISubScreen<*>.getSharedSavedStateRegistryOwner(): SavedStateRegistryOwner {
    getJourneyFragment()?.let {
        debugViewModel("getSharedSavedStateRegistryOwner")
        return it
    }
    if (this is Fragment) {
        Log.e(
            Constant.TAG,
            "getSharedSavedStateRegistryOwner:   SubScreen ${this::class.java.simpleName} is NOT inside a journey"
        )
        return requireActivity()
    }
    throw RuntimeException("SubScreen ${this::class.java.simpleName} is not a fragment")
}

fun ISubScreen<*>.injectJourneyNavigator(): Lazy<J2BaseNavigator> {
    return lazy(LazyThreadSafetyMode.NONE) {
        debugViewModel("injectJourneyNavigator")
        getSharedViewModelStoreOwner().getViewModel(clazz = J2BaseNavigator::class, qualifier = qualifier(sdk.journeyName)) //use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getViewModel(qualifier = qualifier(sdk.journeyName)) //todo try for 3.1.6
    }
}

fun ISubScreen<*>.injectJourneyNavigatorState(): Lazy<J2BaseNavigator> {
    return lazy(LazyThreadSafetyMode.NONE) {
        debugViewModel("injectJourneyNavigatorState")
        getSharedSavedStateRegistryOwner().getStateViewModel(clazz = J2BaseNavigator::class, qualifier = qualifier(sdk.journeyName))//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getStateViewModel(qualifier = qualifier(sdk.journeyName))//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> ISubScreen<*>.injectJourneyViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectJourneyViewModel")
        getSharedViewModelStoreOwner().getViewModel()//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> ISubScreen<*>.injectJourneyViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectJourneyViewModelState")
        getSharedSavedStateRegistryOwner().getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getStateViewModel()//todo try for 3.1.6
    }
}

//----------//----------//---------- screen view model

private fun ISubScreen<*>.getScreenFragment(): Fragment? {
    if (this is Fragment) {
        //search for iScreen
        var checkThisFragment: Fragment? = this
        do {
            val parent = checkThisFragment?.parentFragment
            (parent as? IScreen<*>)?.let {
                if (it is Fragment) {
                    return it
                }
            }
            checkThisFragment = parent
        } while (checkThisFragment != null && checkThisFragment !is IJourney<*> && checkThisFragment !is NavHostFragment && checkThisFragment !is IScreen<*>)
    }
    return null
}

fun ISubScreen<*>.screenFragmentOrCrash(): Fragment {
    getScreenFragment()?.let {
        return it
    }
    throw RuntimeException("Screen ${this::class.java.simpleName} is not a fragment")
}

fun ISubScreen<*>.getScreenViewModelStoreOwner(): ViewModelStoreOwner {
    getScreenFragment()?.let {
        debugViewModel("getSharedViewModelStoreOwner")
        return it
    }
    if (this is Fragment) {
        Log.e(
            Constant.TAG,
            "getSharedViewModelStoreOwner:   SubScreen ${this::class.java.simpleName} is NOT inside a screen"
        )
        return requireActivity()
    }
    throw RuntimeException("getSharedViewModelStoreOwner: SubScreen ${this::class.java.simpleName} is not a fragment")
}

fun ISubScreen<*>.getScreenSavedStateRegistryOwner(): SavedStateRegistryOwner {
    getScreenFragment()?.let {
        debugViewModel("getSharedSavedStateRegistryOwner")
        return it
    }
    if (this is Fragment) {
        Log.e(
            Constant.TAG,
            "getSharedViewModelStoreOwner:   SubScreen ${this::class.java.simpleName} is NOT inside a screen"
        )
        return requireActivity()
    }
    throw RuntimeException("getSharedViewModelStoreOwner: SubScreen ${this::class.java.simpleName} is not a fragment")
}

inline fun <reified T : ViewModel> ISubScreen<*>.injectScreenViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectScreenViewModel")
        getScreenViewModelStoreOwner().getViewModel()//use this way for both 2.2.1 and 2.1.6
//        screenFragmentOrCrash().getViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> ISubScreen<*>.injectScreenViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectScreenViewModelState")
        getScreenSavedStateRegistryOwner().getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//        screenFragmentOrCrash().getStateViewModel()//todo try for 3.1.6
    }
}

//endregion
