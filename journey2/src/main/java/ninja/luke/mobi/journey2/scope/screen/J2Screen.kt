package ninja.luke.mobi.journey2.scope.screen

import androidx.databinding.ViewDataBinding
import ninja.luke.mobi.journey2.j2base.*
import ninja.luke.mobi.journey2.scope.J2Sdk

abstract class J2ScreenFragment<Route : J2ScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int = 0
) : J2BaseFragment<Route>(layout), IScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2ScreenDialog<Route : J2ScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int = 0
) : J2BaseDialog<Route>(layout), IScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2ScreenBottomSheet<Route : J2ScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int = 0
) : J2BaseBottomSheet<Route>(layout), IScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

//----------//----------//----------//----------//----------//----------//----------
//----------//----------//---------- binding

abstract class J2ScreenFragmentBinding<Binding : ViewDataBinding, Route : J2ScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int
) : J2BaseFragmentBinding<Binding, Route>(layout), IScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2ScreenDialogBinding<Binding : ViewDataBinding, Route : J2ScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int
) : J2BaseDialogBinding<Binding, Route>(layout), IScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2ScreenBottomSheetBinding<Binding : ViewDataBinding, Route : J2ScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int
) : J2BaseBottomSheetBinding<Binding, Route>(layout), IScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}