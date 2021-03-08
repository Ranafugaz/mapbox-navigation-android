package com.mapbox.navigation.ui.maps.internal.camera.data

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.navigation.ui.maps.R

class MapboxNavigationViewportDataSourceDebugger(
    context: Context,
    private val mapView: MapView
) {
    private val followingPointsSourceId = "mbx_viewport_data_source_following_points_source"
    private val followingPointsLayerId = "mbx_viewport_data_source_following_points_layer"

    private val mapboxMap = mapView.getMapboxMap()
    var enabled = false
        set(value) {
            field = value
            if (value) {
                mapView.addView(paddingBorder)
            } else {
                mapView.removeView(paddingBorder)
                mapboxMap.getStyle()?.removeStyleLayer(followingPointsLayerId)
                mapboxMap.getStyle()?.removeStyleSource(followingPointsSourceId)
            }
        }
    private val paddingBorder = View(context).apply {
        val params = FrameLayout.LayoutParams(mapView.width, mapView.height)
        layoutParams = params
        background = ContextCompat.getDrawable(context, R.drawable.viewport_debugger_border)
    }

    internal fun visualizeFollowingPadding(padding: EdgeInsets) {
        val params = paddingBorder.layoutParams
        params.width = (mapView.width - padding.left - padding.right).toInt()
        params.height = (mapView.height - padding.top - padding.bottom).toInt()
        paddingBorder.layoutParams = params
        paddingBorder.x = padding.left.toFloat()
        paddingBorder.y = padding.top.toFloat()
    }

    internal fun visualizeFollowingPoints(points: List<Point>) {
        val lineString = LineString.fromLngLats(points)
        val style = mapboxMap.getStyle()
        if (enabled && style != null && style.fullyLoaded) {
            if (!style.styleSourceExists(followingPointsSourceId)) {
                val source = GeoJsonSource(
                    GeoJsonSource.Builder(followingPointsSourceId).geometry(lineString)
                )
                style.addSource(source)
            }

            if (!style.styleLayerExists(followingPointsLayerId)) {
                val layer = LineLayer(followingPointsLayerId, followingPointsSourceId).apply {
                    lineColor(Color.CYAN)
                    lineWidth(5.0)
                }
                style.addLayerAbove(layer, "road-label")
            }

            val source = style.getSource(followingPointsSourceId) as GeoJsonSource
            source.geometry(lineString)
        }
    }
}
