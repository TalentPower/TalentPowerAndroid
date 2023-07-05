package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreDocumentSubcolection
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState


class RouteRepositoryImp(val database: FirebaseFirestore) : RouteRepository{

    private lateinit var listener:ListenerRegistration

    override fun createRoute(route: Route,startStop:Stop, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(route.name)
            .set(route)
            .addOnSuccessListener {
                createStop(route.name,startStop){ stop->
                    when(stop){
                        is UiState.Success->{
                            result.invoke(UiState.Success("Route created Success"))
                        }
                        is UiState.Failure->{
                            result.invoke(UiState.Failure(stop.error))
                        }
                        is UiState.Loading->{

                        }

                        else -> {}
                    }

                }
        }.addOnFailureListener {
            result.invoke(UiState.Failure(it.localizedMessage))
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

    override fun getStops(idRoute: String, result: (UiState<List<Stop>>) -> Unit) {
        val query=database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(idRoute)
            .collection(FireStoreDocumentSubcolection.STOPS)

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

    override fun stopQueryListener() {
        listener.remove()
    }




}