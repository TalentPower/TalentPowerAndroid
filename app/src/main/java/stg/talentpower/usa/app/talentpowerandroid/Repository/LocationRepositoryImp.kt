package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("MissingPermission")
class LocationRepositoryImp (
    val location: FusedLocationProviderClient,
    val geocoder:Geocoder,
    val database: FirebaseFirestore
    ): LocationRepository{

    override suspend fun lastLocation():LatLng = suspendCancellableCoroutine{ continuation->
            location.lastLocation.addOnSuccessListener {
                val location=LatLng(it.latitude,it.longitude)
               continuation.resume(location)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }

    override suspend fun geocodeLatLng(obj: LatLng): String = suspendCancellableCoroutine{ continuation->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                geocoder.getFromLocation(obj.latitude,obj.longitude,1,object : Geocoder.GeocodeListener{
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isEmpty()) {
                            continuation.resume("Sin direcciones")
                        } else
                            for (address in addresses) {
                                continuation.resume(address.getAddressLine(0).toString())
                            }
                    }
                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                    }
                })
            }catch (e:Exception){
                continuation.resumeWithException(e)
            }
        }else{
            try {
                val addressList2 = geocoder.getFromLocation(obj.latitude, obj.longitude, 1)
                if (addressList2.isNullOrEmpty()) {
                    continuation.resume("Sin direcciones")
                } else
                    for (address in addressList2) {
                        continuation.resume(address.getAddressLine(0).toString())
                    }
            }catch (e:Exception){
                continuation.resumeWithException(e)
            }
        }
    }


}