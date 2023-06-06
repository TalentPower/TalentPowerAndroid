package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Employess
import stg.talentpower.usa.app.talentpowerandroid.Repository.AuthRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.ClientRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val repository: ClientRepository
): ViewModel() {

    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>> get() = _register

    fun registerClient(email: String, password: String, client: Client) {
        _register.value = UiState.Loading
        repository.registerClient(
            email = email,
            password = password,
            client = client
        ) { _register.value = it }
    }

}