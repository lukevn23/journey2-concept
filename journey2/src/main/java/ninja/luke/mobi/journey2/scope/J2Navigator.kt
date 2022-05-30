package ninja.luke.mobi.journey2.scope

import androidx.lifecycle.SavedStateHandle
import ninja.luke.mobi.journey2.j2base.J2BaseNavigator

abstract class J2Navigator(savedStateHandle: SavedStateHandle? = null) :
    J2BaseNavigator(savedStateHandle)