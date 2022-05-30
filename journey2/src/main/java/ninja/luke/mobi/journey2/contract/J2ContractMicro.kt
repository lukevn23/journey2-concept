package ninja.luke.mobi.journey2.contract

import android.os.Bundle

interface J2ContractMicro<Route: J2Route> {

    val sdk: J2ContractSdk<*>
    val navigator: J2ContractNavigator?
    val route: Route?
    val microArguments: Bundle?

}
