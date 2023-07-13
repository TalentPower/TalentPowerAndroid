package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import stg.talentpower.usa.app.talentpowerandroid.Model.LocationEvent
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("MissingPermission")
class LocationRepositoryImp (
    val location: FusedLocationProviderClient,
    val locationManager:LocationManager,
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

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow { //we can use this, when we need a callback function or response coming frequently.
            //here in this case our Client FusedLocationProviderClient will give callback response frequently whenever something triggered.
            /*
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing Location Permission.")
            }

             */
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            /*
            if (!isGpsEnabled || !isNetworkEnabled) {
                throw LocationClient.LocationException("Gps is Disabled.")
            }

             */

            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(res: LocationResult) {
                    super.onLocationResult(res)
                    res.locations.lastOrNull()?.let { location ->
                        launch { send(location) } //it will send to kotlin flow
                    }
                }
            }


            location.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose { //this will block the flow until it's scope closed.
                location.removeLocationUpdates(locationCallback)
            }
        }
    }


}