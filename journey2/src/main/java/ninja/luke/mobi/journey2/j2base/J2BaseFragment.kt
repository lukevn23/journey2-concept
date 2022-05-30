package ninja.luke.mobi.journey2.j2base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ninja.luke.mobi.journey2.contract.J2ContractFragment
import ninja.luke.mobi.journey2.contract.J2Route

abstract class J2BaseFragment<Route : J2Route>(
    protected val layout: Int = 0
) : Fragment(), J2BaseMicro<Route>, J2ContractFragment {

    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (layout > 0) return inflater.inflate(layout, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigator
        super.onViewCreated(view, savedInstanceState)
        afterOnViewCreated()
    }

    override fun onStart() {
        super.onStart()
        afterOnStart()
    }

    override fun onResume() {
        super.onResume()
        afterOnResume()
    }

    override fun onPause() {
        beforeOnPause()
        super.onPause()
    }

    override fun onStop() {
        beforeOnStop()
        super.onStop()
    }

    override fun onDestroy() {
        beforeOnDestroy()
        super.onDestroy()
    }

}