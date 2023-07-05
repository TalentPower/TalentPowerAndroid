package stg.talentpower.usa.app.talentpowerandroid.Repository

import com.google.firebase.auth.AuthResult
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.Employee
import stg.talentpower.usa.app.talentpowerandroid.Model.EmployeeClient
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

interface AuthRepository {
    suspend fun registerUser(email: String, password: String, user: Employee, onResult: (UiState<String>) -> Unit)
    suspend fun updateUserInfo(user: Employee, result: (UiState<String>) -> Unit)
    suspend fun loginUser(email: String, password: String,result: (UiState<Any>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String,fcm:String ,result: (Any?) -> Unit)
    suspend fun getSession(result: (Any?) -> Unit)
    suspend fun getEmployee(userid:String,FCM:String):Employee?
    suspend fun getDriver(userid:String,FCM:String): Driver2?
    suspend fun getEmployeeClient(userid:String,FCM:String): EmployeeClient?
    suspend fun getFCMDeviceToken():String

}