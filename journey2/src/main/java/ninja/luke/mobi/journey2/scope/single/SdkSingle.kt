package ninja.luke.mobi.journey2.scope.single

import ninja.luke.mobi.journey2.contract.J2Route
import org.koin.core.module.Module

object SdkSingle : J2SdkSingle<J2Route>() {

    override fun initDefaultModuleOfViewModels(): Module? = null

}