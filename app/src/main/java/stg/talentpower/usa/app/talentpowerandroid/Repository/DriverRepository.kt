package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.LocationEvent
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface DriverRepository {
    fun registerDriver(email: String, password: String, driver: Driver, result: (UiState<String>) -> Unit)
    fun updateDriverInfo(driver: Driver2)
    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    suspend fun getDrivers(onResult: (UiState<List<Driver2>>) -> Unit)
    suspend fun updateLocation(idUser:String,location:LocationEvent)
    suspend fun checkInOut(idUser:String,idRoute:String,isCheked:Boolean,onResult:(UiState<String>)-> Unit)
    suspend fun getLocalDriver(onResult: (Driver2?) -> Unit)
    fun checkStatus(idRoute:String):Flow<UiState<Int>>

    suspend fun getPoints(idRoute:String):List<Stop>?

    fun logout(result: () -> Unit)
}