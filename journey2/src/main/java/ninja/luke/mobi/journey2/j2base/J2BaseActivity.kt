package ninja.luke.mobi.journey2.j2base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ninja.luke.mobi.journey2.contract.J2ContractActivity

abstract class J2BaseActivity : AppCompatActivity, J2ContractActivity {
    constructor()
    constructor(layout: Int) : this() {
        this.layout = layout
    }

    private var layout: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSetupContentView()
    }

    open fun handleSetupContentView() {
        if (layout > 0) {
            setContentView(layout)
        }
    }

    private var _queryExtras: Bundle? = null
    override val queryExtras: Bundle?
        get() {
            _queryExtras?.let {
                return it
            }
            intent.extras?.let {
                _queryExtras = Bundle().apply {
                    this.putAll(it)
                }
                return _queryExtras
            }
            return null
        }

}
