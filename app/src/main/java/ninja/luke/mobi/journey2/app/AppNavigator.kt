package ninja.luke.mobi.journey2.app

import ninja.luke.mobi.journey2.app.screen.easy.EasyRoute
import ninja.luke.mobi.journey2.app.screen.home.HomeRoute
import ninja.luke.mobi.journey2.app.screen.single2.Single2Route
import ninja.luke.mobi.journey2.app.screen.single3.Single3Route
import ninja.luke.mobi.journey2.contract.J2ContractNavigator.*
import ninja.luke.mobi.journey2.scope.J2Navigator

class AppNavigator : J2Navigator(), HomeRoute, EasyRoute, Single2Route, Single3Route {

    override fun onHomeGoEasy() {
        offerNavEvent(NextScreen(R.id.action_home_to_easy))
    }

    override fun onEasyGoSingle1() {
        offerNavEvent(NextScreen(R.id.action_easy_to_single1))
    }

    override fun onEasyGoSingle2() {
        offerNavEvent(NextScreen(R.id.action_easy_to_single2))
    }

    override fun onEasyGoSingle3() {
        offerNavEvent(NextScreen(R.id.action_easy_to_single3))
    }

    override fun onSingle2ButtonClick() {
        offerNavEvent(PopScreen())
    }

    override fun onSingle3GoNextOnLiveData() {
        viewModelScopeLaunch {
            sendNavEvent(PopScreen())
        }
    }

}