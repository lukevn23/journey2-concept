package ninja.luke.mobi.journey2.scope.journey

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ninja.luke.mobi.journey2.BuildConfig
import ninja.luke.mobi.journey2.Constant
import ninja.luke.mobi.journey2.j2base.J2BaseNavigator
import ninja.luke.mobi.journey2.contract.*
import ninja.luke.mobi.journey2.contract.J2ContractNavigator.*
import ninja.luke.mobi.journey2.j2base.J2BaseMicro
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.qualifier.qualifier
import java.lang.Exception
import java.lang.RuntimeException

interface J2JourneyRoute : J2Route

interface IJourney<Route : J2JourneyRoute> : J2BaseMicro<Route>, J2ContractFragment,
    J2NavListener {

    //override val sdk: J2BaseSdk<*>
    override val navigator: J2BaseNavigator
    override val route: Route? get() = searchForRouteInParent()
    override val microArguments: Bundle? get() = mergeArguments()


    //----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- sdk init/destroy
    override fun beforeOnCreate() = initSDK(asFragment!!.requireActivity())
    fun initSDK(fragmentActivity: FragmentActivity) = sdk.init(fragmentActivity)
    override fun beforeOnDestroy() {
        handleBackPressUnregister()
        destroySDK(asFragment?.activity)
    }
    fun destroySDK(fragmentActivity: FragmentActivity?) = sdk.destroy(fragmentActivity)


    //----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- handle back
    //registration
    val shouldHandleBackPressEnabled: Boolean get() = true
    val shouldHandleBackPressAutomaticallyFinishThisJourneyActivity: Boolean get() = true
    fun handleBackPressRegisterListener(dialog: Dialog? = null) =
        doHandleBackPressRegisterListener(dialog)

    fun handleBackPressUnregister() = doUnregisterHandleBackPressed()

    //on click
    var handleBackPressOnClick: Any?
    val shouldHandleBackPressAutomaticallyPopScreenOnParent: Boolean get() = true
    fun onHandleBackPressed(): Boolean {
        onNavEventReceived(PopScreen())
        return true
    }


    //----------//----------//----------//----------//----------//----------//----------
    //----------//----------//----------//region nav event
    val navHost: NavHostFragment?
        get() = ((asFragment?.childFragmentManager?.findFragmentById(sdk.journeyId)) as? NavHostFragment)
    val nav: NavController? get() = navHost?.findNavController()
    override fun afterOnViewCreated() {
        asFragment?.viewLifecycleOwner?.lifecycleScope?.launchWhenCreated {
            for (event in navigator.navigation) {
                onNavEventReceived(event)
            }
        }
    }

    override fun onNavEventReceived(event: J2NavEvent) {
        when (event) {
            is NextScreen -> onNextScreen(event.action, event.extras)
            is PopScreen -> if (!onJourneyPop(event.action, event.extras)) {
                debugRoute("XXX No back button handler on this journey, recommend to add a function on Route on${this::class.java.simpleName}Cancelled() and call it when super.onJourneyPop() return failed")
            }
            is NotImplementedYet -> notImplemented()
            else -> notRecognized()
        }
    }

    fun onNextScreen(action: Int, extras: Bundle?): Boolean {
        try {
            nav?.navigate(action, extras)
            return true
        } catch (e: Exception) {
            Log.e(Constant.TAG, "onNextScreen:   error", e)
        }
        return false
    }

    //this is default implementation of AGuideNavigator.PopScreen
    fun onJourneyPop(action: Int, extras: Bundle? = null): Boolean {
        nav?.let { n ->
            n.previousBackStackEntry?.let {
                n.currentBackStackEntry?.let {
                    if (action > 0) {
                        n.popBackStack(action, true)
                        return true
                    } else {
                        if (n.popBackStack()) return true
                        n.previousBackStackEntry
                        n.navigate(it.destination.id)//reload here
                    }
                }
            }
        }
        if (shouldHandleBackPressAutomaticallyFinishThisJourneyActivity
            && letFinishActivityIfAny(extras)
        ) return true
        if (shouldHandleBackPressAutomaticallyPopScreenOnParent
            && letDeliverPopScreenToParentIfAny()
        ) return true
        return false
    }

    fun notImplemented() {
        asFragment?.context?.let {
            AlertDialog.Builder(it).setMessage("Not Implemented Yet!!!")
                .setNeutralButton("OK", null).create().show()
        }
    }

    fun notRecognized() {
        asFragment?.context?.let {
            AlertDialog.Builder(it).setMessage("Action Not Recognized!!!")
                .setNeutralButton("OK", null).create().show()
        }
    }

    private fun letDeliverPopScreenToParentIfAny(extras: Bundle? = null): Boolean {
        (asFragment?.parentFragment?.parentFragment as? IJourney<*>)?.let {
            it.onNavEventReceived(PopScreen(extras = extras))
            return true
        }
        return false
    }

    private fun letFinishActivityIfAny(extras: Bundle? = null): Boolean {
        asFragment?.activity?.let { activity ->
            if (sdk.activityName == activity::class.java.name) {
                debugRoute("My [" + this::class.java.simpleName + "] is started by [" + activity::class.java.simpleName + "]. Finished it with data")
                val data = Intent()
                extras?.let { data.putExtras(it) }
                activity.setResult(Activity.RESULT_OK, data)
                activity.finish()
                return true
            }
        }
        return false
    }
    //endregion

}

//----------//----------//----------//----------//----------//----------//----------
//----------//----------//---------- handle back

// registration
private fun IJourney<*>.doHandleBackPressRegisterListener(dialog: Dialog? = null) {
    if (this is DialogFragment) {
        dialog?.setOnShowListener { mDialog ->
            val d = mDialog as Dialog
            val bottomSheet =
                d.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(it).skipCollapsed = true
                BottomSheetBehavior.from(it).isHideable = !shouldHandleBackPressEnabled
            }
            val touchOutsideView =
                d.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
            touchOutsideView?.setOnClickListener {
                if (shouldHandleBackPressEnabled) {
                    onHandleBackPressed()
                } else {
                    dialog.cancel()
                }
            }
        }
        dialog?.setCancelable(!shouldHandleBackPressEnabled)
        (dialog as? BottomSheetDialog)?.setCanceledOnTouchOutside(!shouldHandleBackPressEnabled)

        if (!shouldHandleBackPressEnabled) return
        val onBackPressed: DialogInterface.OnKeyListener =
            (handleBackPressOnClick as? DialogInterface.OnKeyListener)
                ?: DialogInterface.OnKeyListener { _, keyCode, event ->
                    Log.d(
                        "Lu",
                        "Dialog.onKey:   ${event.action}   $keyCode   ${event.isTracking}   ${event.isCanceled}"
                    )
                    if (event.action == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)
                    ) {
                        Log.d("Lu", "Dialog.onKey.DOWN.BACK/ESCAPE: ")
                        return@OnKeyListener true
                    }
                    if (event.action == KeyEvent.ACTION_UP &&
                        (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)
                        && !event.isCanceled
                    ) {
                        Log.e("Lu", "Dialog.onKey.UP.BACK/ESCAPE: ")
                        onHandleBackPressed()
                        return@OnKeyListener true
                    }
                    return@OnKeyListener false
                }
        dialog?.setOnKeyListener(onBackPressed)
        handleBackPressOnClick = onBackPressed
    } else if (this is Fragment) {
        if (!shouldHandleBackPressEnabled) return
        val onBackPress: OnBackPressedCallback =
            (handleBackPressOnClick as? OnBackPressedCallback) ?: object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onHandleBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPress)
        handleBackPressOnClick = onBackPress
    }
}

private fun IJourney<*>.doUnregisterHandleBackPressed() {
    if (!shouldHandleBackPressEnabled) return
    if (this is DialogFragment) {
        //do nothing, just clear onBackPressed
        handleBackPressOnClick = null
    } else if (this is Fragment) {
        val onBackPress = handleBackPressOnClick as? OnBackPressedCallback
        onBackPress?.remove()
        handleBackPressOnClick = null
    }
}


//----------//----------//---------- implementation route
//region route
private fun IJourney<*>.debugRoute(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "journeyRoute:   $message", e)
        return
    }
    if (BuildConfig.DEBUG) {
        Log.d(Constant.TAG, "journeyRoute:   $message")
    }
}


@Suppress("UNCHECKED_CAST")
private fun <Route : J2JourneyRoute> IJourney<Route>.searchForRouteInParent(): Route? {
    debugRoute("check-custom")
//    sdk.customRoute?.let {
//        return it
//    }
    debugRoute("is-fragment")
    (this as? Fragment)?.let { fragment ->
        val debugStart = "Journey [" + this::class.java.simpleName + "] is started"

//        debugRoute("check-activity")
//        fragment.activity?.let { activity ->
//            if (sdk.activityName == activity::class.java.name) {
//                debugRoute("$debugStart by Activity [" + activity::class.java.simpleName + "]. Finished it with data")
//                return sdk.routeFromStartForResultCallback
//            }
//        }

        debugRoute("check-parent")
        fragment.parentFragment?.let { firstParent ->
            if (firstParent is NavHostFragment) {
                debugRoute("inside-navhost   check-parent-2")
                firstParent.parentFragment?.let { secondParent ->
                    debugRoute("inside-navhost   inside-fragment")
                    return searchForRouteInJourneyFragment(debugStart, secondParent)
                }
                debugRoute("inside-navhost   check-activity")
                (firstParent.activity as? Route)?.let {
                    debugRoute("$debugStart inside Activity [" + it::class.java.simpleName + "]. Let it handles")
                    return it
                }
            } else {
                debugRoute("inside-fragment")
                return searchForRouteInJourneyFragment(debugStart, firstParent)
            }
        }
    }

    debugRoute("${this::class.java.simpleName}'s Route not found")
    return null
}

@Suppress("UNCHECKED_CAST")
private fun <Route : J2JourneyRoute> IJourney<Route>.searchForRouteInJourneyFragment(
    debugStart: String,
    fragment: Fragment
): Route? {
    if (fragment is IJourney<*>) {
        debugRoute("inside-another-journey")
        try {
            val route =
                fragment.navigator//getViewModel(clazz = AGuideNavigator::class, qualifier = qualifier(fragment::class.java.name))//use this way for both 2.2.1 & 2.1.6
            (route as? Route)?.let {
                debugRoute("$debugStart Journey [" + fragment::class.java.simpleName + "]. Let their navigator handles")
                return it
            }
        } catch (e: Exception) {
            debugRoute(
                "XXX $debugStart inside Journey [" + fragment::class.java.simpleName + "]. BUT navigator is NOT provided",
                e
            )
        }
    } else {
        debugRoute("$debugStart a strange Fragment [" + fragment::class.java.simpleName + "]")
    }
    return null
}
//endregion

//----------//----------//---------- implementation extras
//region extras
private fun IJourney<*>.mergeArguments(): Bundle? {
    var extras: Bundle? = null
    extractBundleArgumentsFromActivity()?.let {
        extras = Bundle().apply {
            putAll(it)
        }
    }
    (this as? Fragment)?.arguments?.let {
        if (extras == null) extras = Bundle()
        extras?.putAll(it)
    }
    return extras
}

private fun IJourney<*>.extractBundleArgumentsFromActivity(): Bundle? {
    (this as? Fragment)?.activity?.let { act ->
        if (act.javaClass.name == sdk.activityName
            || act.javaClass.simpleName.equals("J2Activity")
        ) {
            (act as? J2ContractActivity)?.let {
                it.queryExtras?.let { bundle ->
                    return Bundle().apply {
                        this.putAll(bundle)
                    }
                }
            }
            act.intent.extras?.let { bundle ->
                return Bundle().apply {
                    this.putAll(bundle)
                }
            }
        }
    }
    return null
}
//endregion

//----------//----------//---------- implementation view model
//region ViewModel
private fun IJourney<*>.debugViewModel(message: String, e: Exception? = null) {
    e?.let {
        Log.e(Constant.TAG, "journeyVM:   $message", e)
        return
    }
    if (BuildConfig.DEBUG) {
        Log.d(Constant.TAG, "journeyVM:   $message")
    }
}

fun IJourney<*>.asFragmentOrCrash(): Fragment {
    if (this is Fragment) {
        debugViewModel("getJourneyVMStoreOwner")
        return this
    }
    throw RuntimeException("Journey ${this::class.java.simpleName} is not a fragment")
}

fun IJourney<*>.getSharedViewModelStoreOwner(): ViewModelStoreOwner {
    if (this is Fragment) {
        debugViewModel("getJourneyVMStoreOwner")
        return this
    }
    throw RuntimeException("Journey ${this::class.java.simpleName} is not a fragment")
}

fun IJourney<*>.getSharedSavedStateRegistryOwner(): SavedStateRegistryOwner {
    if (this is Fragment) {
        debugViewModel("getJourneySSRegistryOwner")
        return this
    }
    throw RuntimeException("Journey ${this::class.java.simpleName} is not a fragment")
}

private fun IJourney<*>.getJourneyNavigator(): J2BaseNavigator {
    return getSharedViewModelStoreOwner().getViewModel(
        clazz = J2BaseNavigator::class,
        qualifier = qualifier(sdk.journeyName)
    )  //use this way for both 2.2.1 and 2.1.6
//    return asFragmentOrCrash().getViewModel(qualifier = qualifier(sdk.journeyName))//todo try for 3.1.6
}

private fun IJourney<*>.getJourneyNavigatorState(): J2BaseNavigator {
    return getSharedSavedStateRegistryOwner().getStateViewModel(
        clazz = J2BaseNavigator::class,
        qualifier = qualifier(sdk.journeyName)
    )//use this way for both 2.2.1 and 2.1.6
//    return asFragmentOrCrash().getStateViewModel(qualifier = qualifier(sdk.journeyName))//todo try for 3.1.6
}

fun IJourney<*>.injectJourneyNavigator(): Lazy<J2BaseNavigator> {
    return lazy(LazyThreadSafetyMode.NONE) {
        debugViewModel("injectJourneyNavigator")
        val journeyArguments = microArguments
        getJourneyNavigator().apply {
            microArguments = journeyArguments
        }
    }
}

fun IJourney<*>.injectJourneyNavigatorState(): Lazy<J2BaseNavigator> {
    return lazy(LazyThreadSafetyMode.NONE) {
        debugViewModel("injectJourneyNavigatorState")
        val journeyArguments = microArguments
        getJourneyNavigatorState().apply {
            microArguments = journeyArguments
        }
    }
}

inline fun <reified T : ViewModel> IJourney<*>.injectJourneyViewModel(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "journeyVM:   injectJourneyViewModel")
        getSharedViewModelStoreOwner().getViewModel()//use this way for both 2.2.1 and 2.1.6
//        asFragmentOrCrash().getViewModel()//todo try for 3.1.6
    }
}

inline fun <reified T : ViewModel> IJourney<*>.injectJourneyViewModelState(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        Log.d(Constant.TAG, "journeyVM:   injectJourneyViewModelState")
        getSharedSavedStateRegistryOwner().getStateViewModel()//use this way for both 2.2.1 and 2.1.6
//        asFragmentOrCrash().getStateViewModel()//todo try for 3.1.6
    }
}
//endregion
