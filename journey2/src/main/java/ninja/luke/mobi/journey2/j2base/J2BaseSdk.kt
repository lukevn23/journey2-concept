package ninja.luke.mobi.journey2.j2base

import android.content.Context
import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import ninja.luke.mobi.journey2.BuildConfig
import ninja.luke.mobi.journey2.Constant
import ninja.luke.mobi.journey2.contract.J2ContractNavigator
import ninja.luke.mobi.journey2.contract.J2Route
import ninja.luke.mobi.journey2.contract.J2ContractSdk
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.setIsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.*
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.lang.ref.WeakReference
import java.util.*

abstract class J2BaseSdk<Route : J2Route> : J2ContractSdk<Route>,
    J2ContractNavigator.J2NavListener {

    var config: Module? = null
    private var moduleOfUtils: Module? = null
    private var moduleOfServices: Module? = null
    private var moduleOfUseCases: Module? = null
    private var moduleOfViewModels: Module? = null
    private var moduleForOverride: Module? = null

    private var disableLifeCount = false
    fun setFlagForDisablingLifeCountWhichMayLeadToCrash(flag: Boolean = false) {
        disableLifeCount = flag
    }

    private var lifeCount = 0

    //----------//----------//----------//----------//----------//----------//----------//----------
    //----------//----------//----------

    @CallSuper
    override fun init(context: Context) {
        lifeCount++
        startKoinIfNotStarted(context)
        loadModules()
    }

    protected open fun loadModules() {
        Log.d(Constant.TAG, "${this::class.java.simpleName}.loadModules.lifeCount:  $lifeCount")
        if (disableLifeCount || lifeCount > 0) {
            loadModuleOfUtils()
            loadModuleOfServices()
            loadModuleOfUseCases()
            loadModuleOfViewModels()
            loadModuleForOverride()
        }
    }

    @CallSuper
    override fun destroy(context: Context?) {
        lifeCount--
        unloadModules()
    }

    open fun unloadModules() {
        Log.d(Constant.TAG, "${this::class.java.simpleName}.unloadModules.lifeCount:  $lifeCount")
        if (disableLifeCount || lifeCount <= 0) {
            moduleForOverride?.let {
                unloadKoinModules(it)
                moduleForOverride = null
            }
            moduleOfViewModels?.let {
                unloadKoinModules(it)
                moduleOfViewModels = null
            }
            moduleOfUseCases?.let {
                unloadKoinModules(it)
                moduleOfUseCases = null
            }
            moduleOfServices?.let {
                unloadKoinModules(it)
                moduleOfServices = null
            }
            moduleOfUtils?.let {
                unloadKoinModules(it)
                moduleOfUtils = null
            }
        }
    }

    //----------//----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- private functions - don't try to edit it!!!

    protected open fun startKoinIfNotStarted(context: Context) {
        if (KoinContextHandler.getOrNull() == null) {//use this way for 2.1.6
//        if (GlobalContext.getOrNull() == null) {//todo try for 3.1.6
            startKoin {
                androidLogger(Level.INFO)
                androidContext(context.applicationContext)
            }
        }
    }

    protected open fun loadModuleOfUtils() {
        if (moduleOfUtils == null) {
            moduleOfUtils = initDefaultModuleOfUtils()
            moduleOfUtils?.let {
                loadKoinModules(it)
            }
        }
    }

    protected open fun loadModuleOfServices() {
        if (moduleOfServices == null) {
            moduleOfServices = initDefaultModuleOfServices()
            moduleOfServices?.let {
                loadKoinModules(it)
            }
        }
    }

    protected open fun loadModuleOfUseCases() {
        if (moduleOfUseCases == null) {
            moduleOfUseCases = initDefaultModuleOfUseCases()
            moduleOfUseCases?.let {
                loadKoinModules(it)
            }
        }
    }

    protected open fun loadModuleOfViewModels() {
        if (moduleOfViewModels == null) {
            moduleOfViewModels = initDefaultModuleOfViewModels()
            //default implementation to provide an empty navigator
            if (moduleOfViewModels == null) {
                if (BuildConfig.DEBUG) {
                    Log.e("Jou", "${this::class.java.simpleName}.loadModuleOfViewModels:  You have NOT YET PROVIDE navigator for this journey ${journeyName.split('.').last()}")
                }
                moduleOfViewModels = module(override = true) {
                    viewModel<J2BaseNavigator>(qualifier(journeyName)) { J2NavigatorEmpty() }
                }
            }
            moduleOfViewModels?.let {
                loadKoinModules(it)
            }
        }
    }

    protected open fun loadModuleForOverride() {
        if (moduleForOverride == null) {
            moduleForOverride = config
            moduleForOverride?.let {
                loadKoinModules(it)
            }
        }
    }

    protected open fun initDefaultModuleOfUtils(): Module? = null

    protected open fun initDefaultModuleOfServices(): Module? = null

    protected open fun initDefaultModuleOfUseCases(): Module? = null

    protected abstract fun initDefaultModuleOfViewModels(): Module?

    private class J2NavigatorEmpty : J2BaseNavigator()

    //----------//----------//----------//----------//----------//----------//----------//----------
    //----------//----------//---------- support functions

    protected fun Module.journeyNavigator(
        definition: Definition<J2BaseNavigator>
    ): BeanDefinition<J2BaseNavigator> {
        return journeyViewModel(qualifier = qualifier(journeyName), definition = definition)
    }

    protected inline fun <reified T : ViewModel> Module.journeyViewModel(
        qualifier: Qualifier? = qualifier(journeyName),//use this way for 2.1.6
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = factory(qualifier, override, definition)
        beanDefinition.setIsViewModel()
        return beanDefinition
    }

    protected inline fun <reified T : ViewModel> Module.screenViewModel(
        qualifier: Qualifier? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        val beanDefinition = factory(qualifier, override, definition)
        beanDefinition.setIsViewModel()
        return beanDefinition
    }



    //----------//----------//----------//----------//----------//----------//----------//----------
    //----------//----------//----------//region listen navigation
    //the following functions/parameters is used for IJourney ONLY

    private var _listenerOnNavDestination: WeakReference<J2ContractNavigator.J2NavListener>? = null
    private val _listenersOnNavDestination: Hashtable<String, WeakReference<J2ContractNavigator.J2NavListener>> by lazy { Hashtable() }

    val listenerOnNavDestination: J2ContractNavigator.J2NavListener?
        get() = _listenerOnNavDestination?.get()

    fun registerListenerOnNavDestination(listener: J2ContractNavigator.J2NavListener) {
        _listenersOnNavDestination[listener::class.java.name] = WeakReference(listener)
        _listenerOnNavDestination = WeakReference(listener)
    }

    //endregion
}