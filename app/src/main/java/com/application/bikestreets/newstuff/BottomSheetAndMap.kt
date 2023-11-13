package com.application.bikestreets.newstuff

import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.application.bikestreets.api.modals.Location
import com.application.bikestreets.api.modals.Route
import com.application.bikestreets.bottomsheet.BottomSheetContentState
import com.application.bikestreets.composables.ActionButtonsContainer
import com.application.bikestreets.composables.BottomSheet
import com.application.bikestreets.composables.BottomSheetContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetAndMap(onSettingsClicked: (() -> Unit)) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    val mapboxMapController = remember { MapboxMapController() }
    val routes = remember { mutableStateOf<List<Route>>(listOf()) }

    var bottomSheetContentState by remember { mutableStateOf(BottomSheetContentState.INITIAL) }

    /**
     * Sheet can either be changed due to swipe (in BottomSheet)
     * or by closing via the "X" in BottomSheetContent
     */
    fun modifySheetScaffoldState(newValue: BottomSheetValue) {
        coroutineScope.launch {
            if (newValue == BottomSheetValue.Collapsed) {
                bottomSheetScaffoldState.bottomSheetState.collapse()

                //TODO: Close keyboard if currently open
            } else {
                bottomSheetScaffoldState.bottomSheetState.expand()
            }
        }
    }

    /**
     * Used to change the sizing and content shown on bottom sheet
     */
    fun modifySheetContentState(newContentState: BottomSheetContentState) {
        bottomSheetContentState = newContentState
    }

    BottomSheet(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetContent(
                onSearchPerformed = { origin: Location?, destination: Location? ->
                    coroutineScope.launch {
                        val newRoutes = mapboxMapController.updateMapForSearch(origin, destination)
                        routes.value = newRoutes
                    }
                },
                routes = routes.value,
                notifyRouteChosen = { route -> {} },
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                onCloseClicked = { modifySheetScaffoldState(BottomSheetValue.Collapsed) },
                bottomSheetContentState = bottomSheetContentState,
                onBottomSheetContentChange = { newContentState ->
                    modifySheetContentState(
                        newContentState
                    )
                }
            )
        },
        actionButtons = {
            ActionButtonsContainer(
                onSettingsButtonClicked = { onSettingsClicked() },
                onLocationButtonClicked = {
                    mapboxMapController.centerOnCurrentLocation()
                }
            )
        },
        bottomSheetContentState = bottomSheetContentState,
    ) { MapboxMap(mapboxMapController) }
}
