package ninja.luke.mobi.journey2.contract

import androidx.fragment.app.Fragment

interface J2ContractFragment {

    //as fragment
    val asFragment: Fragment? get() = this as? Fragment
    fun beforeOnCreate() = Unit
    fun beforeOnDestroy() = Unit
    fun afterOnViewCreated() = Unit
    fun afterOnStart() = Unit
    fun afterOnResume() = Unit
    fun beforeOnPause() = Unit
    fun beforeOnStop() = Unit


}



