package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import stg.talentpower.usa.app.talentpowerandroid.Model.Employess
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.SharedPrefConstants
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState


class AuthRepositoryImp(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val appPreferences:SharedPreferences,
    val gson: Gson
): AuthRepository {




    override fun registerUser(email: String, password: String, user: Employess, result: (UiState<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    user.id = it.result.user?.uid ?: ""
                    updateUserInfo(user) { state ->
                        when(state){
                            is UiState.Success -> {
                                Log.d("test","state success in updateuser")
                                /*
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null){
                                        result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                    }else{
                                        result.invoke(
                                            UiState.Success("User register successfully!")
                                        )
                                    }
                                }


                                 */
                                result.invoke(UiState.Success("User register successfully!"))
                            }
                            is UiState.Failure -> {
                                result.invoke(UiState.Failure(state.error))
                            }

                            UiState.Loading -> {

                            }
                        }
                    }
                }else{
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateUserInfo(user: Employess, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USERS).document(FireStoreCollection.EMPLOYESS)
        document
            .update(hashMapOf(user.id to user) as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("test","User has been update successfully")
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                Log.d("test","on succeslistener")
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: ""){
                        if (it == null){
                            result.invoke(UiState.Failure("Failed to store local session"))
                        }else{
                            Log.d("storageLocation","User isnt null")
                            result.invoke(UiState.Success(task.result.user?.uid.toString()))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email and password"))
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

    override fun storeSession(id: String, result: (Employess?) -> Unit) {
        database.collection(FireStoreCollection.USERS).document(FireStoreCollection.EMPLOYESS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val document=it.result
                    if (document.exists()){
                        val queue=document.get(id) as Map<*,*>
                        if (queue.containsValue(id)){
                            val user=Employess(
                                queue["id"].toString(),
                                queue["email"].toString(),
                                queue["password"].toString(),
                                queue["name"].toString(),
                                queue["phone"].toString(),
                                queue["rol"].toString(),)
                            appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(user)).apply()
                            result.invoke(user)
                        }
                    }

                    /*
                    if (document!=null){
                        val obj=document.toObject(DocumentEmployes::class.java)
                        if (obj!=null) {
                            obj.employess?.forEach {
                                Log.d("listaEmployess", it.toString())
                            }
                        }else Log.d("listaEmployess", "el la lista es null")
                    }else Log.d("listaEmployess", "El document es null")

                     */
                    /*
                    it.result.data?.forEach {map->

                        try {
                            val res= it.result.data?.
                            Log.d("testStoresesion", res.toString())

                            //result.invoke(res)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }


                     */
                }else{
                    result.invoke(null)
                }


            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (Employess?) -> Unit) {
        Log.d("storeSession","Entre a store sesion")
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (user_str == null){
            Log.d("storeSession","User str null")
            result.invoke(null)
        }else{
            Log.d("storeSession","User_str not null")

            val user = gson.fromJson(user_str,Employess::class.java)
            result.invoke(user)
        }
    }
}