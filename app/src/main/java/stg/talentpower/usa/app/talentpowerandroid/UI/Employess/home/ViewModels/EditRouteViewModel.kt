package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Repository.LocationRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class EditRouteViewModel @Inject constructor(
    private val firebase:RouteRepository,
    private val location: LocationRepository,
): ViewModel() {

    private val _start = MutableLiveData<LatLng>()
    val start:LiveData<LatLng> get() = _start

    private val _end = MutableLiveData<LatLng>()
    val end:LiveData<LatLng>get() = _end

    private var _latlng=MutableLiveData<LatLng>()
    val latLng:LiveData<LatLng> get() = _latlng

    private var _registerRoute= MutableLiveData<UiState<String>>()
    val registerRoute:LiveData<UiState<String>> get() = _registerRoute

    private val _routesList = MutableLiveData<UiState<List<Route>>?>()
    val routesList: MutableLiveData<UiState<List<Route>>?> get() = _routesList

    private var _LastLocation= MutableLiveData<LatLng>()
    val lastLocation:LiveData<LatLng> get() = _LastLocation

    private var _address=MutableLiveData<String>()
    val addres:LiveData<String> get() = _address

    private val _getStops = MutableLiveData<UiState<List<Stop>>>()
    val stops: LiveData<UiState<List<Stop>>> get() = _getStops

    private val _stopStatus = MutableLiveData<UiState<String>>()
    val stopStatus: LiveData<UiState<String>> get() = _stopStatus

    fun setStart(data:LatLng){
        Log.d("latnlg","Hice el setStart $data")
        _start.postValue(data)
        Log.d("latnlg","value del start ${start.value}")
    }

    fun setEnd(data: LatLng){
        _end.postValue(data)
    }

    fun getRoutes(){
        viewModelScope.launch {
            firebase.getRoutes {
                _routesList.postValue(it)
            }
        }
    }

    fun getStops(id:String){
        viewModelScope.launch {
            firebase.getStops(id){
                _getStops.value=it
            }
        }
    }

    fun lastLocation(){
        viewModelScope.launch {
            _LastLocation.postValue(location.lastLocation())
        }
    }

    fun geocodeLatLng(obj:LatLng){
        _latlng.value=obj
        viewModelScope.launch {
            _address.postValue(location.geocodeLatLng(obj))
        }
    }

    fun saveRoute(idRoute:String,idStop:String,hr:String){
        _registerRoute.value=UiState.Loading
        val route= Stop()
        route.name=idStop
        route.time=hr
        val point= GeoPoint(latLng.value!!.latitude, latLng.value!!.longitude)
        route.location=point
        route.address= addres.value!!
        firebase.createStop(idRoute,route){
            _registerRoute.value=it
        }
    }

    fun deleteStop(idRoute:String,obj: Stop) {
        firebase.deleteStop(idRoute,obj){
            _stopStatus.value=it
        }
    }

    fun stopListener(){
        firebase.stopQueryListener()
    }

}