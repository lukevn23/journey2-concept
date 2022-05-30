package ninja.luke.mobi.journey2.scope.single

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.NavHostFragment
import androidx.savedstate.SavedStateRegistryOwner
import ninja.luke.mobi.journey2.BuildConfig
import ninja.luke.mobi.journey2.Constant
import ninja.luke.mobi.journey2.contract.J2ContractFragment
import ninja.luke.mobi.journey2.contract.J2Route
import ninja.luke.mobi.journey2.j2base.J2BaseMicro
import ninja.luke.mobi.journey2.j2base.J2BaseNavigator
import ninja.luke.mobi.journey2.j2base.J2BaseSdk
import ninja.luke.mobi.journey2.scope.journey.IJourney
import ninja.luke.mobi.journey2.scope.screen.IScreen
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.lang.Exception
import java.lang.RuntimeException

interface J2SingleRoute : J2Route

interface ISingle<Route : J2SingleRoute> : J2BaseMicro<Route>, J2ContractFragment {

    override val sdk: J2BaseSdk<*>
    override val navigator: J2BaseNavigator? get() = null
    override val route: Route? get() = searchForRouteInParent()
    override val microArguments: Bundle? get() = mergeArguments()


    //----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- sdk init/destroy
    override fun beforeOnCreate() = initSDK(asFragment!!.requireActivity())
    fun initSDK(fragmentActivity: FragmentActivity) = sdk.init(fragmentActivity)
    override fun beforeOnDestroy() = destroySDK(asFragment?.activity)
    fun destroySDK(fragmentActivity: FragmentActivity?) = sdk.destroy(fragmentActivity)

}


//----------//----------//---------- implementation route

private fun ISingle<*>.debugRoute(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "singleRoute:   $message", e)
        return
    }
    if (BuildConfig.DEBUG) {
        Log.d(Constant.TAG, "singleRoute:   $message")
    }
}

@Suppress("UNCHECKED_CAST")
private fun <Route : J2SingleRoute> ISingle<Route>.searchForRouteInParent(): Route? {
    if (this is Fragment) {
        var checkThisFragment: Fragment? = this
        do {
            val parent = checkThisFragment?.parentFragment
            (parent as? Route)?.let {
                return it
            }
            checkThisFragment = parent
        } while (checkThisFragment != null && checkThisFragment !is IJourney<*> && checkThisFragment !is NavHostFragment)
        if (checkThisFragment == null) {
            val message =
                "Single ${this::class.java.simpleName} is belong to an activity's fragment. Cannot route to anywhere"
            debugRoute(message)
            return null
            //throw RuntimeException(message)
        }
        if (checkThisFragment is NavHostFragment) checkThisFragment = checkThisFragment.parentFragment
        if (checkThisFragment is IJourney<*>) {
            val journeyNavigator = checkThisFragment.navigator
            (journeyNavigator as? Route)?.let { simpleRoute ->
                return simpleRoute
            }
            val message =
                "${journeyNavigator::class.java.simpleName} does NOT YET implement ${this::class.java.simpleName}'s Route. It's mandatory for routing screens."
            debugRoute(message)
            return null
            //throw RuntimeException(message)
        }
    }
    val message =
        "${this::class.java.simpleName} is NOT a fragment."
    debugRoute(message)
    return null
    //throw RuntimeException(message)
}

//----------//----------//---------- implementation extras

private fun ISingle<*>.mergeArguments() : Bundle? {
    var extras: Bundle? = null
    (this as? Fragment)?.arguments?.let {
        if (extras == null) extras = Bundle()
        extras?.putAll(it)
    }
    return extras
}


//----------//----------//----------//----------//----------//----------//----------//----------
//----------//----------//---------- view model

private fun ISingle<*>.debugViewModel(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "singleVM:   $message", e)
        return
    }
}

//----------//----------//---------- journey view model
//region journey view model
private fun ISingle<*>.getJourneyFragment(): Fragment? {
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
fun ISingle<*>.journeyFragmentOrCrash(): Fragment {
    getJourneyFragment()?.let {
        return it
    }
    throw RuntimeException("Single ${this::class.java.simpleName} is not a fragment")
}


fun ISingle<*>.getSharedViewModelStoreOwner(): ViewModelStoreOwner {
    getJourneyFragment()?.let {
        debugViewModel("getSharedViewModelStoreOwner")
        return it
    }
    if (this is Fragment) {
        Log.e(Constant.TAG, "getSharedViewModelStoreOwner:   Single ${this::class.java.simpleName} is NOT inside a journey")
        return requireActivity()
    }
    throw RuntimeException("getSharedViewModelStoreOwner: Single ${this::class.java.simpleName} is not a fragment")
}

fun ISingle<*>.getSharedSavedStateRegistryOwner(): SavedStateRegistryOwner {
    getJourneyFragment()?.let {
        debugViewModel("getSharedSavedStateRegistryOwner")
        return it
    }
    if (this is Fragment) {
        Log.e(Constant.TAG, "getSharedSavedStateRegistryOwner:   Single ${this::class.java.simpleName} is NOT inside a journey")
        return requireActivity()
    }
    throw RuntimeException("Single ${this::class.java.simpleName} is not a fragment")
}

inline fun <reified T : ViewModel> ISingle<*>.injectJourneyViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "singleVM:   injectJourneyViewModel")
        getSharedViewModelStoreOwner().getViewModel()//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> ISingle<*>.injectJourneyViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "singleVM:   injectJourneyViewModelState")
        getSharedSavedStateRegistryOwner().getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//        journeyFragmentOrCrash().getStateViewModel()//todo try for 3.1.6
    }
}

//endregion

//----------//----------//---------- screen view model
//region screem view model
private fun ISingle<*>.getScreenFragment(): Fragment? {
    if (this is Fragment) {
        //search for iScreen
        var checkThisFragment: Fragment? = this
        do {
            checkThisFragment = checkThisFragment?.parentFragment
            (checkThisFragment as? IScreen<*>)?.let {
                if (it is Fragment) {
                    return it
                }
            }
        } while (checkThisFragment != null && checkThisFragment !is IJourney<*> && checkThisFragment !is NavHostFragment && checkThisFragment !is IScreen<*>)
        if (checkThisFragment is NavHostFragment) {
            Log.e(Constant.TAG, "getScreenFragment:   Single ${this::class.java.simpleName} is inside a NavHostFragment, treats itself as a screen")
            return this
        }
        Log.e(Constant.TAG, "getScreenFragment:   Single ${this::class.java.simpleName} is inside a strange place, treats itself as a screen")
        return this
    }
    return null
}
fun ISingle<*>.screenFragmentOrCrash(): Fragment {
    getScreenFragment()?.let {
        return it
    }
    throw RuntimeException("Single ${this::class.java.simpleName} is not a fragment")
}

fun ISingle<*>.getScreenViewModelStoreOwner(): ViewModelStoreOwner {
    getScreenFragment()?.let {
        debugViewModel("getSharedViewModelStoreOwner")
        return it
    }
    throw RuntimeException("getSharedViewModelStoreOwner: Single ${this::class.java.simpleName} is not a fragment")
}

fun ISingle<*>.getScreenSavedStateRegistryOwner(): SavedStateRegistryOwner {
    getScreenFragment()?.let {
        debugViewModel("getSharedSavedStateRegistryOwner")
        return it
    }
    throw RuntimeException("getSharedViewModelStoreOwner: Single ${this::class.java.simpleName} is not a fragment")
}

inline fun <reified T : ViewModel> ISingle<*>.injectScreenViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectScreenViewModel")
        getScreenViewModelStoreOwner().getViewModel()//use this way for both 2.2.1 and 2.1.6
//        screenFragmentOrCrash().getViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> ISingle<*>.injectScreenViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "screenVM:   injectScreenViewModelState")
        getScreenSavedStateRegistryOwner().getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//        screenFragmentOrCrash().getStateViewModel()//todo try for 3.1.6
    }
}
//endregion