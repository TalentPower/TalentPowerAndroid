package stg.talentpower.usa.app.talentpowerandroid.Repository

import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface WorkerRepository {

    fun getWorkers(result:(UiState<List<Worker>>) -> Unit)
    fun getRouteWorkers(idRoute:String,result:(UiState<List<Worker>>) -> Unit)
    fun getWorker(id:String,result: (UiState<Worker>) -> Unit)
    fun editWorker(id:String,result: (UiState<String>) -> Unit)
    fun deleteWorker(worker:Worker,result: (UiState<String>) -> Unit)
    fun addWorker(worker:Worker,onResult: (UiState<String>) -> Unit)
    suspend fun clearListWorkers(idRoute: String,numRec:Int,tempList:List<Worker>)
    fun checkFacturationWorker()

}