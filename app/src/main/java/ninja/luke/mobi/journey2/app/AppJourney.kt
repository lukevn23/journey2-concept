package ninja.luke.mobi.journey2.app

import ninja.luke.mobi.journey2.scope.journey.J2JourneyFragment

class AppJourney : J2JourneyFragment<AppRoute>(
    AppSdk,
    R.layout.journey_app
)