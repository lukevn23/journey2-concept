package ninja.luke.mobi.journey2.j2base

import ninja.luke.mobi.journey2.contract.J2ContractMicro
import ninja.luke.mobi.journey2.contract.J2Route

interface J2BaseMicro<Route: J2Route> : J2ContractMicro<Route> {

    override val sdk: J2BaseSdk<*>
    override val navigator: J2BaseNavigator?

}