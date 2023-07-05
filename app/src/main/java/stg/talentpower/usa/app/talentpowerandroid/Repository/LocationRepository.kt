package stg.talentpower.usa.app.talentpowerandroid.Repository

import com.google.android.gms.maps.model.LatLng
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface LocationRepository {

    suspend fun lastLocation():LatLng

    suspend fun geocodeLatLng(obj:LatLng):String

}