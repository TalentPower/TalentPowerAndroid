package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.setings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Employee
import stg.talentpower.usa.app.talentpowerandroid.Repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class SetingsViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    private val _employee=MutableLiveData<Employee>()
    val employee:LiveData<Employee> get() = _employee


    fun getDataUser(){
        viewModelScope.launch (Dispatchers.IO){
            repository.getSession { data->
                when(data){
                    is Employee->{
                        _employee.postValue(data)
                    }
                }
            }
        }
    }

    fun logOut(){
        repository.logout { }
    }


}