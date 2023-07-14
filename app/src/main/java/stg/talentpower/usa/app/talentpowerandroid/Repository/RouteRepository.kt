package stg.talentpower.usa.app.talentpowerandroid.Repository

import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface RouteRepository {
    suspend fun createRoute(route:Route,startStop:Stop,endStop:Stop,result: (UiState<String>) -> Unit)
    fun updateRoute(route: Route, result: (UiState<String>) -> Unit)
    fun deleteRoute(route: Route,result: (UiState<String>) -> Unit)
    fun getRoute(id: String,result: (UiState<Route>) -> Unit)
    fun getRoutes(result: (UiState<List<Route>>) -> Unit)
    fun createStop(idRoute:String, stops:Stop, result: (UiState<String>) -> Unit)
    fun getStops(idRoute:String): Flow<UiState<List<Stop>>>
    fun deleteStop(idRoute:String,stop:Stop,result: (UiState<String>)  -> Unit)
    suspend fun getEndPoint(idRoute: String):Point?
}