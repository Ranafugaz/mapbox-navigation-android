package com.mapbox.navigation.core.trip.session

import android.hardware.SensorEvent
import android.location.Location
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.trip.service.TripService
import com.mapbox.navigation.core.trip.session.eh.EHorizonObserver
import com.mapbox.navigator.FallbackVersionsObserver

internal interface TripSession {

    val tripService: TripService
    val route: DirectionsRoute?
    fun setRoute(route: DirectionsRoute?, legIndex: Int)

    fun getRawLocation(): Location?
    fun getEnhancedLocation(): Location?
    fun getRouteProgress(): RouteProgress?
    fun getState(): TripSessionState

    fun start(withTripService: Boolean)
    fun stop()
    fun isRunningWithForegroundService(): Boolean

    fun registerLocationObserver(locationObserver: LocationObserver)
    fun unregisterLocationObserver(locationObserver: LocationObserver)
    fun unregisterAllLocationObservers()

    fun registerRouteProgressObserver(routeProgressObserver: RouteProgressObserver)
    fun unregisterRouteProgressObserver(routeProgressObserver: RouteProgressObserver)
    fun unregisterAllRouteProgressObservers()

    fun registerOffRouteObserver(offRouteObserver: OffRouteObserver)
    fun unregisterOffRouteObserver(offRouteObserver: OffRouteObserver)
    fun unregisterAllOffRouteObservers()

    fun registerStateObserver(stateObserver: TripSessionStateObserver)
    fun unregisterStateObserver(stateObserver: TripSessionStateObserver)
    fun unregisterAllStateObservers()

    fun registerBannerInstructionsObserver(bannerInstructionsObserver: BannerInstructionsObserver)
    fun unregisterBannerInstructionsObserver(bannerInstructionsObserver: BannerInstructionsObserver)
    fun unregisterAllBannerInstructionsObservers()

    fun registerVoiceInstructionsObserver(voiceInstructionsObserver: VoiceInstructionsObserver)
    fun unregisterVoiceInstructionsObserver(voiceInstructionsObserver: VoiceInstructionsObserver)
    fun unregisterAllVoiceInstructionsObservers()

    fun updateSensorEvent(sensorEvent: SensorEvent)
    fun updateLegIndex(legIndex: Int): Boolean

    fun registerRoadObjectsOnRouteObserver(
        roadObjectsOnRouteObserver: RoadObjectsOnRouteObserver
    )
    fun unregisterRoadObjectsOnRouteObserver(
        roadObjectsOnRouteObserver: RoadObjectsOnRouteObserver
    )
    fun unregisterAllRoadObjectsOnRouteObservers()

    fun registerEHorizonObserver(eHorizonObserver: EHorizonObserver)
    fun unregisterEHorizonObserver(eHorizonObserver: EHorizonObserver)
    fun unregisterAllEHorizonObservers()

    fun registerMapMatcherResultObserver(mapMatcherResultObserver: MapMatcherResultObserver)
    fun unregisterMapMatcherResultObserver(mapMatcherResultObserver: MapMatcherResultObserver)
    fun unregisterAllMapMatcherResultObservers()

    fun registerFallbackVersionsObserver(fallbackVersionsObserver: FallbackVersionsObserver)
    fun unregisterFallbackVersionsObserver(fallbackVersionsObserver: FallbackVersionsObserver)
    fun unregisterAllFallbackVersionsObservers()
}
