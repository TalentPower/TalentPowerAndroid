package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Repository.ClientRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.NotificationRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repositoryRoute: RouteRepository
): ViewModel() {

    private val _routes = MutableLiveData<UiState<List<Route>>?>()
    val routes: MutableLiveData<UiState<List<Route>>?> get() = _routes


    fun getRoutes(){
        viewModelScope.launch {
            repositoryRoute.getRoutes {
                _routes.postValue(it)
            }
        }
    }

}