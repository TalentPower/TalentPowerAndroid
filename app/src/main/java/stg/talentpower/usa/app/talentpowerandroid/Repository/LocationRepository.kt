package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import stg.talentpower.usa.app.talentpowerandroid.Model.LocationEvent
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface LocationRepository {
    suspend fun lastLocation():LatLng
    suspend fun geocodeLatLng(obj:LatLng):String
    fun getLocationUpdates(interval:Long): Flow<Location>
}