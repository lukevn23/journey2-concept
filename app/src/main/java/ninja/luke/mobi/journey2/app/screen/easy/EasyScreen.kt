package ninja.luke.mobi.journey2.app.screen.easy

import android.os.Bundle
import android.view.View
import ninja.luke.mobi.journey2.app.AppSdk
import ninja.luke.mobi.journey2.app.R
import ninja.luke.mobi.journey2.scope.screen.J2ScreenFragment

class EasyScreen : J2ScreenFragment<EasyRoute>(
    AppSdk,
    R.layout.app_screen_easy
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.single1).setOnClickListener {
            route?.onEasyGoSingle1()
        }
        view.findViewById<View>(R.id.single2).setOnClickListener {
            route?.onEasyGoSingle2()
        }
        view.findViewById<View>(R.id.single3).setOnClickListener {
            route?.onEasyGoSingle3()
        }
    }
}