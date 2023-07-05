package stg.talentpower.usa.app.talentpowerandroid.Repository

import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface RouteRepository {
    fun createRoute(route:Route,startStop:Stop,result: (UiState<String>) -> Unit)
    fun updateRoute(route: Route, result: (UiState<String>) -> Unit)
    fun deleteRoute(route: Route,result: (UiState<String>) -> Unit)
    fun getRoute(id: String,result: (UiState<Route>) -> Unit)
    fun getRoutes(result: (UiState<List<Route>>) -> Unit)
    fun createStop(idRoute:String, stops:Stop, result: (UiState<String>) -> Unit)
    fun getStops(idRoute:String,result: (UiState<List<Stop>>)  -> Unit)
    fun deleteStop(idRoute:String,stop:Stop,result: (UiState<String>)  -> Unit)
    fun stopQueryListener()
}