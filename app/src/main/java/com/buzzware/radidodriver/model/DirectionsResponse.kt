package com.buzzware.radidodriver.model

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>
)

data class Route(
    @SerializedName("legs") val legs: List<Leg>,
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline
)

data class Leg(
    @SerializedName("steps") val steps: List<Step>
)

data class Step(
    @SerializedName("polyline") val polyline: OverviewPolyline
)

data class OverviewPolyline(
    @SerializedName("points") val points: String
)
