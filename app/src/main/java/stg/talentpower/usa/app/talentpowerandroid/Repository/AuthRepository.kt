package stg.talentpower.usa.app.talentpowerandroid.Repository

import stg.talentpower.usa.app.talentpowerandroid.Model.Employess
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface AuthRepository {
    fun registerUser(email: String, password: String, user: Employess, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: Employess, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (Employess?) -> Unit)
    fun getSession(result: (Employess?) -> Unit)

}