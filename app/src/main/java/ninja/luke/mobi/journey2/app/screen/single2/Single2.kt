package ninja.luke.mobi.journey2.app.screen.single2

import android.os.Bundle
import android.view.View
import ninja.luke.mobi.journey2.app.R
import ninja.luke.mobi.journey2.scope.single.J2SingleFragment

class Single2 : J2SingleFragment<Single2Route>(
    layout = R.layout.app_screen_single2
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.click).setOnClickListener {
            if (it.isEnabled) {
                it.isEnabled = false
                route?.onSingle2ButtonClick()
            }
        }
    }
}