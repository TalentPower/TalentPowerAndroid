package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject


@HiltViewModel
class AddDriverViewModel @Inject constructor(
    val repository: DriverRepository
): ViewModel() {


    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>> get() = _register

    fun registerDriver(email: String, password: String, driver: Driver){
        _register.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO){
            repository.registerDriver(
                email = email,
                password = password,
                driver = driver
            ) {
                _register.postValue(it)
            }
        }

    }

    fun onUploadMultipleFile(fileUris: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit){
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            repository.uploadMultipleFile(fileUris,onResult)
        }
    }
}