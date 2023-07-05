package stg.talentpower.usa.app.talentpowerandroid.UI.Login.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import stg.talentpower.usa.app.talentpowerandroid.Model.Employee
import stg.talentpower.usa.app.talentpowerandroid.Repository.AuthRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository:AuthRepository
):ViewModel() {

    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>> get() = _register

    private val _login = MutableLiveData<UiState<Any>>()
    val login: LiveData<UiState<Any>> get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>> get() = _forgotPassword


    fun register(email: String, password: String, user: Employee) {
        _register.value=UiState.Loading
        viewModelScope.launch (Dispatchers.IO){
            repository.registerUser(
                email = email,
                password = password,
                user = user
            ) { _register.postValue(it) }
        }

    }

    fun login(email: String, password: String) {
        _login.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repository.loginUser(email, password){
                _login.postValue(it)
            }
        }

    }

    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email){
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun getSession(result: (Any?) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            repository.getSession(result)
        }

    }

}