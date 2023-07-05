package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.WorkerRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    val repositoryWorker:WorkerRepository,
    val repositoryRoute:RouteRepository
) : ViewModel() {

    private val _realtimeWorkers = MutableLiveData<UiState<List<Worker>>>()
    val realtimeWorkers: LiveData<UiState<List<Worker>>> get() = _realtimeWorkers

    private val _addWorker = MutableLiveData<UiState<String>?>()
    val addWorker: MutableLiveData<UiState<String>?> get() = _addWorker

    private val _stops = MutableLiveData<UiState<List<Stop>>>()
    val stops: LiveData<UiState<List<Stop>>> get() = _stops

    fun getRouteWorkers(idRoute:String){
        _realtimeWorkers.value=UiState.Loading
        repositoryWorker.getRouteWorkers(idRoute) {
            _realtimeWorkers.value=it
        }
    }

    fun addWorker(worker: Worker){
        _addWorker.value=UiState.Loading
        repositoryWorker.addWorker(worker){
            _addWorker.value=it
        }
    }

    fun clearList(idRoute: String,numRecl:Int,lists: List<Worker>) {
        viewModelScope.launch {
            repositoryWorker.clearListWorkers(idRoute,numRecl,lists)
        }

    }

    fun getStops(id:String){
        _stops.value=UiState.Loading
        viewModelScope.launch {
            repositoryRoute.getStops(id){
                _stops.value=it
            }
        }
    }

    fun setNullWorker(){
        _addWorker.value=null
    }

}