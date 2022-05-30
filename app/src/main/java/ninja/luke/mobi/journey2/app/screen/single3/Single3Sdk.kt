package ninja.luke.mobi.journey2.app.screen.single3

import ninja.luke.mobi.journey2.scope.single.J2SdkSingle
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object Single3Sdk : J2SdkSingle<Single3Route>() {

    override fun initDefaultModuleOfViewModels(): Module = module {
        viewModel { Single3ViewModel() }
    }
}