package ninja.luke.mobi.journey2.scope.single

import androidx.databinding.ViewDataBinding
import ninja.luke.mobi.journey2.j2base.*
import ninja.luke.mobi.journey2.scope.J2Sdk

abstract class J2SingleFragment<Route : J2SingleRoute>(
    override val sdk: J2Sdk<*> = SdkSingle,
    layout: Int = 0
) : J2BaseFragment<Route>(layout), ISingle<Route>

abstract class J2SingleDialog<Route : J2SingleRoute>(
    override val sdk: J2Sdk<*> = SdkSingle,
    layout: Int = 0
) : J2BaseDialog<Route>(layout), ISingle<Route>

abstract class J2SingleBottomSheet<Route : J2SingleRoute>(
    override val sdk: J2Sdk<*> = SdkSingle,
    layout: Int = 0
) : J2BaseBottomSheet<Route>(layout), ISingle<Route>

//----------//----------//----------//----------//----------//----------//----------
//----------//----------//---------- binding

abstract class J2SingleFragmentBinding<Binding : ViewDataBinding, Route : J2SingleRoute>(
    override val sdk: J2Sdk<*> = SdkSingle,
    layout: Int
) : J2BaseFragmentBinding<Binding, Route>(layout), ISingle<Route>

abstract class J2SingleDialogBinding<Binding : ViewDataBinding, Route : J2SingleRoute>(
    override val sdk: J2Sdk<*> = SdkSingle,
    layout: Int
) : J2BaseDialogBinding<Binding, Route>(layout), ISingle<Route>

abstract class J2SingleBottomSheetBinding<Binding : ViewDataBinding, Route : J2SingleRoute>(
    override val sdk: J2Sdk<*> = SdkSingle,
    layout: Int
) : J2BaseBottomSheetBinding<Binding, Route>(layout), ISingle<Route>
