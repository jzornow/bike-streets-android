package com.application.bikestreets.newstuff

import android.content.Context
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.application.bikestreets.R
import com.application.bikestreets.utils.mapTypeFromPreferences
import com.application.bikestreets.utils.moveCamera
import com.application.bikestreets.utils.showMapLayers
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import kotlinx.coroutines.launch

@Composable
fun MapboxMap(mapboxMapController: MapboxMapController) {
    val coroutineScope = rememberCoroutineScope()

    // TODO: How to better handle this
    var location: Point


    fun loadMapboxStyle(mapboxMap: MapboxMap, context: Context) {
        coroutineScope.launch {
            var mapStyle = "asset://stylejson/style.json"

            // apply map style conditionally, based on user's preferences.
            if (mapTypeFromPreferences(context).equals(
                    ContextCompat.getString(
                        context,
                        R.string.preference_satellite
                    )
                )
            ) {
                mapStyle = Style.SATELLITE
            }

            // Load style, on compete show layers
            mapboxMap.loadStyleUri(mapStyle) { showMapLayers(context, it) }
        }
    }


    AndroidView(
        factory = { context ->
            MapView(context).also { mapView ->
                // Attribution
                mapView.logo.updateSettings {
                    position = Gravity.TOP
                }
                mapView.attribution.updateSettings {
                    position = Gravity.TOP
                }

                // Hide Scalebar
                mapView.scalebar.updateSettings { enabled = false }

                // Load Style
                mapView.getMapboxMap().also { mapboxMap ->
                    loadMapboxStyle(mapboxMap, context)

                    // Attach map to controller
                    mapboxMapController.attachMapboxMap(mapView, mapboxMap, context)
                }

                // Load Map Markers
                // TODO: do this in a different thread so UI is not blocked
//                mapMarkersManager = MapMarkersManager(mapView)
                // Initialization code if necessary, e.g., setting up listeners, starting loading, etc.
            }
        },
        update = { mapView ->
            // Here, you can update the MapView when the composable recomposes.
            // For instance, you can set the camera position, update the map style, etc.
//            val defaultLocation = Point.fromLngLat(-104.9687837, 39.7326381)
//            moveCamera(mapView.getMapboxMap(), defaultLocation)


        }
    )
}