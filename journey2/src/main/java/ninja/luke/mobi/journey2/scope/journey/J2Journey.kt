package ninja.luke.mobi.journey2.scope.journey

import android.app.Dialog
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import ninja.luke.mobi.journey2.j2base.*
import ninja.luke.mobi.journey2.scope.J2Sdk

abstract class J2JourneyFragment<Route : J2JourneyRoute>(
    override val sdk: J2Sdk<Route>,
    layout: Int = 0
) : J2BaseFragment<Route>(layout), IJourney<Route> {

    override var handleBackPressOnClick: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBackPressRegisterListener()
    }

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2JourneyDialog<Route : J2JourneyRoute>(
    override val sdk: J2Sdk<Route>,
    layout: Int = 0
) : J2BaseDialog<Route>(layout), IJourney<Route> {

    override var handleBackPressOnClick: Any? = null

    override fun onSetupDialog(dialog: Dialog) : Dialog {
        handleBackPressRegisterListener(dialog)
        return dialog
    }

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2JourneyBottomSheet<Route : J2JourneyRoute>(
    override val sdk: J2Sdk<Route>,
    layout: Int = 0
) : J2BaseBottomSheet<Route>(layout), IJourney<Route> {

    override var handleBackPressOnClick: Any? = null

    override fun onSetupDialog(dialog: Dialog) : Dialog {
        handleBackPressRegisterListener(dialog)
        return dialog
    }

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

//----------//----------//----------//----------//----------//----------//----------
//----------//----------//---------- binding

abstract class J2JourneyFragmentBinding<Binding : ViewDataBinding, Route : J2JourneyRoute>(
    override val sdk: J2Sdk<Route>,
    layout: Int
) : J2BaseFragmentBinding<Binding, Route>(layout), IJourney<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2JourneyDialogBinding<Binding : ViewDataBinding, Route : J2JourneyRoute>(
    override val sdk: J2Sdk<Route>,
    layout: Int
) : J2BaseDialogBinding<Binding, Route>(layout), IJourney<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2JourneyBottomSheetBinding<Binding : ViewDataBinding, Route : J2JourneyRoute>(
    override val sdk: J2Sdk<Route>,
    layout: Int
) : J2BaseBottomSheetBinding<Binding, Route>(layout), IJourney<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}