package stg.talentpower.usa.app.talentpowerandroid.Repository

import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface ClientRepository {
    fun registerClient(email: String, password: String, client: Client, result: (UiState<String>) -> Unit)
    fun addClient(client: Client, result: (UiState<String>) -> Unit)
    fun deleteClient(uid:String,result: (UiState<String>) -> Unit)
}