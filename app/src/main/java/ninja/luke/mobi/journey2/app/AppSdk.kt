package ninja.luke.mobi.journey2.app

import ninja.luke.mobi.journey2.scope.J2Sdk
import org.koin.core.module.Module
import org.koin.dsl.module

object AppSdk : J2Sdk<AppRoute>(
    R.id.appJourney,
    AppJourney::class.java.name,
    AppActivity::class.java.name
) {

    override fun initDefaultModuleOfViewModels(): Module = module {
        journeyNavigator { AppNavigator() }
//        journeyViewModel {  }
//        screenViewModel {  }
    }
}