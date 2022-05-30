package ninja.luke.mobi.journey2.contract

import android.content.Context

interface J2ContractSdk<Route : J2Route> {

    val journeyId: Int
    val journeyName: String
    val activityName: String

    fun init(context: Context)
    fun destroy(context: Context?)

}