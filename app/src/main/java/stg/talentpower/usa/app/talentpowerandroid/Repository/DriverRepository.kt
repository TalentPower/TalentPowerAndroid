package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.net.Uri
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface DriverRepository {
    fun registerDriver(email: String, password: String, driver: Driver, result: (UiState<String>) -> Unit)
    fun updateDriverInfo(driver: Driver2)
    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    suspend fun getDrivers(onResult: (UiState<List<Driver2>>) -> Unit)

}