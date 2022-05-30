package ninja.luke.mobi.journey2.j2base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import ninja.luke.mobi.journey2.contract.J2Route

abstract class J2BaseFragmentBinding<ViewBinding : ViewDataBinding, Route : J2Route>(layout: Int) :
    J2BaseFragment<Route>(layout) {

    protected lateinit var viewBinding: ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (layout > 0) {
            viewBinding = DataBindingUtil.inflate(inflater, layout, container, false)
            return viewBinding.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (layout > 0) {
            viewBinding.executePendingBindings()
        }
    }
}

abstract class J2BaseDialogBinding<ViewBinding : ViewDataBinding, Route : J2Route>(layout: Int) :
    J2BaseDialog<Route>(layout) {

    protected lateinit var viewBinding: ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (layout > 0) {
            viewBinding = DataBindingUtil.inflate(inflater, layout, container, false)
            return viewBinding.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (layout > 0) {
            viewBinding.executePendingBindings()
        }
    }
}

abstract class J2BaseBottomSheetBinding<ViewBinding : ViewDataBinding, Route : J2Route>(layout: Int) :
    J2BaseBottomSheet<Route>(layout) {

    protected lateinit var viewBinding: ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (layout > 0) {
            viewBinding = DataBindingUtil.inflate(inflater, layout, container, false)
            return viewBinding.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (layout > 0) {
            viewBinding.executePendingBindings()
        }
    }
}