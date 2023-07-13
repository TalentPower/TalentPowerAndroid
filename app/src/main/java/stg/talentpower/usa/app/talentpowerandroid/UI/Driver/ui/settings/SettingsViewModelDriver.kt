package stg.talentpower.usa.app.talentpowerandroid.UI.Driver.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModelDriver @Inject constructor(
    private val driverRepository:DriverRepository
): ViewModel() {
    fun logOut() {
        driverRepository.logout {  }
    }


}