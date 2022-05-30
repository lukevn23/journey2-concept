package ninja.luke.mobi.journey2.scope

import ninja.luke.mobi.journey2.contract.J2Route
import ninja.luke.mobi.journey2.j2base.J2BaseSdk

abstract class J2Sdk<Route : J2Route>(
    override val journeyId: Int,
    override val journeyName: String,
    override val activityName: String = J2Activity::class.java.name
) : J2BaseSdk<Route>()