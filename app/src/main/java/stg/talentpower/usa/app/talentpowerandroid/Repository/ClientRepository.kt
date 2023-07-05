package stg.talentpower.usa.app.talentpowerandroid.Repository

import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.EmployeeClient
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface ClientRepository {
    suspend fun registerClient(client: Client, onResult: (UiState<String>) -> Unit)
    fun addClient(client: EmployeeClient, result: (UiState<String>) -> Unit)
    fun deleteClient(uid:String,result: (UiState<String>) -> Unit)
    suspend fun getClients(result: (UiState<List<Client>>) -> Unit)
    fun getClientAreas(nameClient:String,onResult: (UiState<List<String>>) -> Unit)
    fun registerEmployeeClient(email: String, password: String, userClient: EmployeeClient, result: (UiState<String>) -> Unit)

}