package ninja.luke.mobi.journey2.app.screen.home

import android.os.Bundle
import android.view.View
import ninja.luke.mobi.journey2.app.AppSdk
import ninja.luke.mobi.journey2.app.R
import ninja.luke.mobi.journey2.scope.screen.J2ScreenFragment

class HomeScreen : J2ScreenFragment<HomeRoute>(
    AppSdk,
    R.layout.app_screen_home
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.easy).setOnClickListener {
            route?.onHomeGoEasy()
        }
    }
}