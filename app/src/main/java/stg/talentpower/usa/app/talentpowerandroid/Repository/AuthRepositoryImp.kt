package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.Employee
import stg.talentpower.usa.app.talentpowerandroid.Model.EmployeeClient
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.EmployessAreas
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.SharedPrefConstants
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImp(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val appPreferences:SharedPreferences,
    val gson: Gson
): AuthRepository {
    override suspend fun registerUser(email: String, password: String, user: Employee, onResult: (UiState<String>) -> Unit) {
        try {
            val task=auth.createUserWithEmailAndPassword(email,password).await()
            user.id=task.user?.uid ?: ""
            updateUserInfo(user = user){state->
                when (state){
                    is UiState.Success->{
                        onResult.invoke(UiState.Success(state.data))
                    }
                    is UiState.Failure->{
                        onResult.invoke(UiState.Failure(state.error))
                    }
                    else -> {}
                }
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            onResult.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            onResult.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
        } catch (e: FirebaseAuthUserCollisionException) {
            onResult.invoke(UiState.Failure("Authentication failed, Email already registered."))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }

    override suspend fun updateUserInfo(user: Employee, result: (UiState<String>) -> Unit) {
        try {
            database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(user.id)
                .set(user)
                .await()
            result.invoke(UiState.Success("User has been update successfully"))
        }catch (e:Exception){
            result.invoke(UiState.Failure(e.localizedMessage))
        }
    }

    override suspend fun loginUser(email: String, password: String,result: (UiState<Any>) -> Unit) {
        try {
            val auth=auth.signInWithEmailAndPassword(email, password).await()
            if (auth!=null){
                val employee=getEmployee(userid = auth.user?.uid?:"",getFCMDeviceToken())
                if (employee==null){
                    val driver=getDriver(userid = auth.user?.uid?:"",getFCMDeviceToken())
                    if (driver==null){
                        val employeeClient=getEmployeeClient(userid = auth.user?.uid?:"",getFCMDeviceToken())
                        employeeClient?.let {
                            appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(it)).apply()
                            result.invoke(UiState.Success(it))
                        }
                    }else driver.let {
                        Log.d("userDriver","$driver")
                        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(it)).apply()
                        result.invoke(UiState.Success(it))
                    }
                }else employee.let { emp->
                    appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(emp)).apply()
                    result.invoke(UiState.Success(emp))
                }
            }
        }catch (e:Exception){
            e.localizedMessage?.let { Log.d("coroutinesTest", it)
            result.invoke(UiState.Failure(it))}
        }

    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("Email has been sent"))
                } else {
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, fcm: String, result: (Any?) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun getSession(result: (Any?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (user_str == null){
            result.invoke(null)
        }else{
            val tempData = gson.fromJson(user_str, Employee::class.java).rol
            Log.d("getSesionRol", tempData)
            when(tempData){
                "driver"->{
                    val driver = gson.fromJson(user_str, Driver2::class.java)
                    result.invoke(driver)
                }
                "employee"->{
                    val employee = gson.fromJson(user_str, Employee::class.java)
                    result.invoke(employee)
                }
                "employeeClient"->{
                    val employeeClient = gson.fromJson(user_str, EmployeeClient::class.java)
                    result.invoke(employeeClient)
                }
            }
        }
    }

    override suspend fun getEmployee(userid:String,FCM:String):Employee?{
        return try {
            Log.d("userUid","UserUid: $userid")
            database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(userid)
                .update("tokenFCM",FCM).await()

            val employees=database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .whereEqualTo("rol","employee")
                .whereEqualTo("id",userid)
                .get()
                .await()
                .toObjects(Employee::class.java)
            employees[0]
        }catch (e:Exception){
            null
        }
    }

    override suspend fun getDriver(userid:String,FCM:String):Driver2?{
        return try {
            val drivers=database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .whereEqualTo("rol","driver")
                .whereEqualTo("id",userid)
                .get()
                .await()
                .toObjects(Driver2::class.java)
            drivers[0]
        }catch (e:Exception){
            null
        }
    }

    override suspend fun getEmployeeClient(userid:String,FCM:String):EmployeeClient?{
        return try {
            val employeeClients=database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .whereEqualTo("rol","employeeClient")
                .whereEqualTo("id",userid)
                .get()
                .await()
                .toObjects(EmployeeClient::class.java)
            employeeClients[0]
        }catch (e:Exception){
            null
        }
    }

    override suspend fun getFCMDeviceToken(): String = FirebaseMessaging.getInstance().token.await()

}