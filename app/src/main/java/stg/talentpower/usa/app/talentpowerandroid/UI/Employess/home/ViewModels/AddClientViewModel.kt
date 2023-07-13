package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Repository.ClientRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.LocationRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class AddClientViewModel @Inject constructor(
    val locationRepository: LocationRepository,
    val repositoryClient: ClientRepository,
): ViewModel() {

    private val _registerClient = MutableLiveData<UiState<String>>()
    val registerClient: LiveData<UiState<String>> get() = _registerClient

    private var _latlng=MutableLiveData<GeoPoint>()
    val latLng:LiveData<GeoPoint> get() = _latlng

    private var _address=MutableLiveData<String>()
    val addres:LiveData<String> get() = _address

    private var _LastLocation= MutableLiveData<LatLng>()
    val lastLocation:LiveData<LatLng> get() = _LastLocation

    fun registerClient( client: Client) {
        _registerClient.value=UiState.Loading
        if (latLng.value!=null)client.latlng=latLng.value
        if (addres.value!!.isNotBlank())client.address= addres.value.toString()
        viewModelScope.launch(Dispatchers.IO) {
            repositoryClient.registerClient(
                client = client)
            { _registerClient.postValue(it) }
        }
    }

    fun geocodeLatLng(obj:LatLng){
        viewModelScope.launch(Dispatchers.IO) {
            _address.postValue(locationRepository.geocodeLatLng(obj))
        }
    }

    fun setLatlng(obj: GeoPoint) {
        _latlng.value=obj
    }

    fun lastLocation(){
        viewModelScope.launch {
            _LastLocation.postValue(locationRepository.lastLocation())
        }
    }

}