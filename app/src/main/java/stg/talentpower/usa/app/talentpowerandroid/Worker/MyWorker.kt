package stg.talentpower.usa.app.talentpowerandroid.Worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection

class MyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val appContext = applicationContext



        return Result.success()
    }

    private fun updateFirebaseStatus() {
        val database= FirebaseFirestore.getInstance()
        var lista= mutableListOf<Client>()
        getClients(database){ list->
            lista= list as MutableList<Client>
        }


        database.collection(FireStoreCollection.WORKERS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO).get().addOnCompleteListener { task->
                if (task.isSuccessful){
                    val doc=task.result
                    val list:MutableList<stg.talentpower.usa.app.talentpowerandroid.Model.Worker> = doc.toObjects(
                        stg.talentpower.usa.app.talentpowerandroid.Model.Worker::class.java)
                    val newList= mutableListOf<stg.talentpower.usa.app.talentpowerandroid.Model.Worker>()
                    list.forEach { worker ->

                    }
                }
            }.addOnFailureListener {

            }
    }

    private fun getClients(database:FirebaseFirestore,result:(List<Client>) -> Unit){
        database.collection(FireStoreCollection.CLIENTS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO).get().addOnCompleteListener { task->
                if (task.isSuccessful){
                    val doc=task.result
                    val list=doc.toObjects(Client::class.java)
                    if (!list.isEmpty()) result.invoke(list)
                }else result.invoke(emptyList())
            }
    }

}