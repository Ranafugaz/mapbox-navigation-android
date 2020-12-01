package com.mapbox.navigation.ui.maps.route.routearrow.internal

import com.mapbox.navigation.ui.maps.R
import com.mapbox.navigation.ui.maps.route.routearrow.api.RouteArrowResourceProvider

internal class MapboxRouteArrowResourceProvider(
    private val routeArrowColor: Int,
    private val routeArrowBorderColor: Int,
) : RouteArrowResourceProvider {
    override fun getArrowBorderColor(): Int = routeArrowBorderColor
    override fun getArrowColor(): Int = routeArrowColor
    override fun getArrowHeadIcon(): Int = R.drawable.mapbox_ic_arrow_head
    override fun getArrowHeadCasingIcon(): Int = R.drawable.mapbox_ic_arrow_head_casing
}