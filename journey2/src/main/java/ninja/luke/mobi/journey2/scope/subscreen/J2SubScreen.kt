package ninja.luke.mobi.journey2.scope.subscreen

import androidx.databinding.ViewDataBinding
import ninja.luke.mobi.journey2.j2base.*
import ninja.luke.mobi.journey2.scope.J2Sdk

abstract class J2SubScreenFragment<Route : J2SubScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int = 0
) : J2BaseFragment<Route>(layout), ISubScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2SubScreenDialog<Route : J2SubScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int = 0
) : J2BaseDialog<Route>(layout), ISubScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2SubScreenBottomSheet<Route : J2SubScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int = 0
) : J2BaseBottomSheet<Route>(layout), ISubScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

//----------//----------//----------//----------//----------//----------//----------
//----------//----------//---------- binding

abstract class J2SubScreenFragmentBinding<Binding : ViewDataBinding, Route : J2SubScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int
) : J2BaseFragmentBinding<Binding, Route>(layout), ISubScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2SubScreenDialogBinding<Binding : ViewDataBinding, Route : J2SubScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int
) : J2BaseDialogBinding<Binding, Route>(layout), ISubScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}

abstract class J2SubScreenBottomSheetBinding<Binding : ViewDataBinding, Route : J2SubScreenRoute>(
    override val sdk: J2Sdk<*>,
    layout: Int
) : J2BaseBottomSheetBinding<Binding, Route>(layout), ISubScreen<Route> {

    override val navigator: J2BaseNavigator by injectJourneyNavigator()

}