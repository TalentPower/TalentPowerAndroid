package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreDocumentSubcolection
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState


class WorkerRepositoryImp(val database: FirebaseFirestore) : WorkerRepository {
    override fun getWorkers(result: (UiState<List<Worker>>) -> Unit) {
        result.invoke(UiState.Loading)
        database.collection(FireStoreCollection.WORKERS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("snapshotListener", "Listen failed.", e)
                    result.invoke(UiState.Failure(e.localizedMessage))
                    return@addSnapshotListener
                }

                if (!snapshot?.isEmpty!!) {
                    Log.d("snapshotListener", "Current data: ${snapshot.documents}")
                    val list= snapshot.toObjects(Worker::class.java)
                    result.invoke(UiState.Success(list))
                } else {
                    Log.d("snapshotListener", "Current data: null")
                }
            }
    }

    override fun getRouteWorkers(idRoute: String) : Flow<UiState<List<Worker>>> {
        return database.collection(FireStoreCollection.WORKERS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO).snapshots().mapNotNull { snapShot->
                UiState.Success(snapShot.toObjects(Worker::class.java))
            }.catch {
                UiState.Failure(it.localizedMessage)
            }
                /*
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(UiState.Failure(e.localizedMessage))
                    return@addSnapshotListener
                }

                if (snapshot?.isEmpty == false) {
                    val snapList= snapshot.toObjects(Worker::class.java)
                    Log.d("snapList",snapList.toString())
                    Log.d("snapList",idRoute)
                    if (snapList.isNotEmpty()){
                        val list= mutableListOf<Worker>()
                        snapList.forEach { worker->
                            if (worker.asignedRoute==idRoute)
                                list.add(worker)
                        }
                        result.invoke(UiState.Success(list))
                    }
                } else {
                    Log.d("snapshotListener", "Current data: null")
                    val list= mutableListOf<Worker>()
                    result.invoke(UiState.Success(list))
                }
            }


                 */
    }

    override fun getWorker(id: String, result: (UiState<Worker>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun editWorker(id: String, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteWorker(worker: Worker, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun addWorker(worker: Worker, onResult: (UiState<String>) -> Unit){
        Log.d("addingWorker","entre a addworker")
        setWorkerToStop(worker){
            database.collection(FireStoreCollection.WORKERS)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO).document(worker.rfc)
                .set(worker)
                .addOnCompleteListener {
                    Log.d("addingWorker","onCompleteListener del addworker")
                    onResult.invoke(UiState.Success("Successful"))
                }.addOnFailureListener {
                    onResult.invoke(UiState.Failure(it.localizedMessage))
                }
        }
    }

    override suspend fun clearListWorkers(idRoute: String, numRec:Int, tempList: List<Worker>) {
        val listWorkersToRemove= mutableListOf<String>()
        val batch: WriteBatch = database.batch()
        val doc=database.collection(FireStoreCollection.WORKERS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)

        tempList.forEach { item->
            listWorkersToRemove.add(item.rfc)
            val documentReference: DocumentReference = doc.document(item.rfc)
            batch.delete(documentReference)
        }

        batch.commit().addOnSuccessListener {
            Log.d("deleteBach","onsuccess")

            val document=database.collection(FireStoreCollection.ROUTES)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO).document(idRoute)

            document.update("noReclWorkers",numRec).addOnCompleteListener {

                document.collection(FireStoreDocumentSubcolection.STOPS)
                    .get()
                    .addOnCompleteListener { task->

                    if (task.isSuccessful){
                        val listRoutes=task.result.toObjects(Stop::class.java)
                        if (listRoutes.isNotEmpty()){
                            listRoutes.forEach { stop->
                                val listB=stop.workers as ArrayList
                                listB.forEach { workerInRoute->
                                    listWorkersToRemove.forEach { workerToRemove->
                                        if (workerInRoute == workerToRemove)
                                            listB.remove(workerInRoute)
                                    }
                                }
                                document.collection(FireStoreDocumentSubcolection.STOPS)
                                    .document(stop.name)
                                    .update("workers",listB)
                                    .addOnCompleteListener {
                                        Log.d("listWorkers","Se actualizo la ruta en")
                                    }
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener {
            Log.d("deleteBach","onfailure")
        }
    }

    override fun checkFacturationWorker() {

    }

    private fun setWorkerToStop(worker: Worker, result: (UiState<String>) -> Unit){
        val ref=database.collection(FireStoreCollection.ROUTES)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(worker.asignedRoute)
        ref.update("noReclWorkers",FieldValue.increment(1)).addOnSuccessListener {
            Log.d("addingWorker","addOnSuccessListener del increment")
            ref.collection(FireStoreDocumentSubcolection.STOPS)
                .document(worker.asignedStop)
                .update("workers",FieldValue.arrayUnion(worker.rfc))
                .addOnCompleteListener {
                    Log.d("addingWorker","addOnSuccessListener del update worker in stop")
                result.invoke(UiState.Success("Set Success"))
            }.addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
        }


    }
}