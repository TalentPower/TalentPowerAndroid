package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.snapshots
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreDocumentSubcolection
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState


class RouteRepositoryImp(val database: FirebaseFirestore, val realtimeDatabase: FirebaseDatabase) : RouteRepository{

    //private lateinit var listener:ListenerRegistration

    @SuppressLint("NewApi")
    override suspend fun createRoute(route: Route, startStop:Stop, endStop:Stop, result: (UiState<String>) -> Unit) {
        try {
            database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(route.idDriver)
                .update("route",route.name)
                .await()

            database.collection(FireStoreCollection.ROUTES)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(route.name)
                .set(route)
                .await()

            database.collection(FireStoreCollection.ROUTES)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(route.name)
                .collection(FireStoreDocumentSubcolection.STOPS)
                .document(startStop.name)
                .set(startStop)
                .await()

            database.collection(FireStoreCollection.ROUTES)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(route.name)
                .collection(FireStoreDocumentSubcolection.STOPS)
                .document(endStop.name)
                .set(endStop)
                .await()



            val hrs=stg.talentpower.usa.app.talentpowerandroid.Util.Date.currentDataObject()!!.hour
            val min=stg.talentpower.usa.app.talentpowerandroid.Util.Date.currentDataObject()!!.minute
            val map = HashMap<String, Any>()
            map["driverId"] = route.idDriver
            map["started"] = "$hrs:$min a.m."
            map["finished"] = "$hrs:$min a.m."
            map["status"]=0
            map["workers"] = mutableListOf<String>()
            realtimeDatabase.getReference("routes_status").child(route.name).setValue(map).await()

            result.invoke(UiState.Success("Ruta creada"))
        }catch (e:Exception){
            result.invoke(UiState.Failure(e.localizedMessage))
        }

    }

    override fun updateRoute(route: Route, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteRoute(route: Route, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getRoute(id: String, result: (UiState<Route>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getRoutes(result: (UiState<List<Route>>) -> Unit) {
        database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .get().addOnCompleteListener { task->
            if (task.isSuccessful){
                val doc=task.result
                val list=doc.toObjects(Route::class.java)
                result.invoke(UiState.Success(list))
            }
        }.addOnFailureListener {
            result.invoke(UiState.Failure(it.localizedMessage))
        }
    }

    override fun createStop(idRoute:String,stops: Stop, result: (UiState<String>) -> Unit) {
        val document=database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(idRoute)
            .collection(FireStoreDocumentSubcolection.STOPS)
            .document(stops.name)
        document.set(stops).addOnSuccessListener {
            result.invoke(UiState.Success("Route created success"))
        }.addOnFailureListener {
            result.invoke(UiState.Failure(it.localizedMessage))
        }
    }

    override fun getStops(idRoute: String): Flow<UiState<List<Stop>>> {
        return  database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(idRoute)
            .collection(FireStoreDocumentSubcolection.STOPS).snapshots().mapNotNull { querySnapshot ->
                UiState.Success(querySnapshot.toObjects(Stop::class.java))
            }.catch {
                UiState.Failure(it.localizedMessage)
            }

        /*
        listener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(UiState.Failure(e.localizedMessage))
                    return@addSnapshotListener
                }
                if (!snapshot?.isEmpty!!) {
                    val snapList= snapshot.toObjects(Stop::class.java)
                    result.invoke(UiState.Success(snapList))
                } else {
                    val list= listOf<Stop>()
                    result.invoke(UiState.Success(list))
                    Log.d("snapshotListener", "Current data: null")
                }
            }

         */

    }

    override fun deleteStop(idRoute: String,stop: Stop, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(idRoute)
            .collection(FireStoreDocumentSubcolection.STOPS).document(stop.name).delete().addOnCompleteListener {
                result.invoke(UiState.Success("Eliminado"))
            }.addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override suspend fun getEndPoint(idRoute: String):Point? {
        return try {
            var temVal=Point.fromLngLat(0.0,0.0)
            database.collection(FireStoreCollection.ROUTES)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(idRoute)
                .collection(FireStoreDocumentSubcolection.STOPS)
                .get()
                .await()
                .toObjects(Stop::class.java).mapNotNull { point->
                    if (point.name.contains("Final")) {
                        point.location.let { location->
                            temVal=Point.fromLngLat(location!!.longitude,location.latitude)
                        }

                    }
                }
            temVal
        }catch (e:Exception){
            null
        }
    }


}