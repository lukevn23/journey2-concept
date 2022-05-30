package ninja.luke.mobi.journey2.scope.single

import ninja.luke.mobi.journey2.contract.J2Route
import ninja.luke.mobi.journey2.j2base.J2BaseSdk
import ninja.luke.mobi.journey2.scope.J2Activity
import ninja.luke.mobi.journey2.scope.J2Sdk

abstract class J2SdkSingle<Route : J2Route>(
    journeyId: Int = 0,
    journeyName: String = "",
    activityName: String = J2Activity::class.java.name
) : J2Sdk<Route>(journeyId, journeyName, activityName)