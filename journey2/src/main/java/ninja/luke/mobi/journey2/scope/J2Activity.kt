package ninja.luke.mobi.journey2.scope

import ninja.luke.mobi.journey2.R
import ninja.luke.mobi.journey2.j2base.J2BaseActivity

/**
 * Use for: journey & single
 */
open class J2Activity : J2BaseActivity {
    constructor() : super(0)
    constructor(layout: Int = R.layout.activity_j2activity) : super(layout)
}