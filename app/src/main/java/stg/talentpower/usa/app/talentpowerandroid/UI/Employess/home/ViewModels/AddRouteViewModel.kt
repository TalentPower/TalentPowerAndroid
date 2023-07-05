package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Repository.ClientRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.LocationRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class AddRouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val repositoryDriver:DriverRepository,
    private val repositoryClient: ClientRepository
): ViewModel(){

    private val _start = MutableLiveData<Stop>()
    val start: LiveData<Stop> get() = _start

    private val _end = MutableLiveData<LatLng>()
    val end: LiveData<LatLng> get() = _end

    private var _registerRoute= MutableLiveData<UiState<String>>()
    val registerRoute:LiveData<UiState<String>> get() = _registerRoute

    private val _listDrivers = MutableLiveData<UiState<List<Driver2>>>()
    val listDrivers: LiveData<UiState<List<Driver2>>> get() = _listDrivers

    private val _listClients = MutableLiveData<UiState<List<Client>>>()
    val listClients: LiveData<UiState<List<Client>>> get() = _listClients


    fun setStart(data:Stop){
        _start.postValue(data)
    }

    fun setEnd(data: LatLng){
        _end.postValue(data)
    }

    fun getDrivers(){
        _listDrivers.value=UiState.Loading
        viewModelScope.launch (Dispatchers.IO){
            repositoryDriver.getDrivers {
                _listDrivers.postValue(it)
            }
        }
    }

    fun getCLients(){
        _listClients.value=UiState.Loading
        viewModelScope.launch (Dispatchers.IO){
            repositoryClient.getClients {
                _listClients.postValue(it)
            }
        }
    }

    fun createRoute(route:Route,stop:Stop){
        _registerRoute.value=UiState.Loading
        viewModelScope.launch (Dispatchers.IO){
            routeRepository.createRoute(route,stop){
                _registerRoute.value=it
            }
        }
    }

    fun updateDriver(selectedDriver: Driver2) {
        repositoryDriver.updateDriverInfo(selectedDriver)
    }


}