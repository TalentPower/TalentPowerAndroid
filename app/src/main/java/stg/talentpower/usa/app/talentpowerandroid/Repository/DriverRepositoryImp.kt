package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.ImagesModel
import stg.talentpower.usa.app.talentpowerandroid.Model.LocationEvent
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.Date
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreDocumentSubcolection
import stg.talentpower.usa.app.talentpowerandroid.Util.FirebaseStorageConstants.NOTE_IMAGES
import stg.talentpower.usa.app.talentpowerandroid.Util.SharedPrefConstants
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState


class DriverRepositoryImp(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val storageReference: StorageReference,
    val realtimeDatabase: FirebaseDatabase,
    val appPreferences: SharedPreferences,
    val gson: Gson
) :DriverRepository{


    override fun registerDriver(email: String, password: String, driver: Driver, result: (UiState<String>) -> Unit){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                driver.id=it.result.user?.uid ?: ""
                updateDriverInfo(driver){state ->
                    when(state){
                        is UiState.Success->{
                            result.invoke(UiState.Success("Driver register successfully!"))
                        }
                        is UiState.Failure->{
                            result.invoke(UiState.Failure(state.error))
                        }
                        is UiState.Loading-> TODO()
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
        }.addOnFailureListener {
            result.invoke(
                UiState.Failure(
                    it.localizedMessage
                )
            )
        }
    }
    override fun updateDriverInfo(driver: Driver2) {
        database.collection(FireStoreCollection.DRIVERS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(driver.id)
            .update("route",driver.route)
            .addOnCompleteListener {
                Log.d("driverUpdated","oncomplete del uodateDriver")
            }.addOnFailureListener {

            }
    }
    fun updateDriverInfo(driver: Driver, result: (UiState<String>) -> Unit) {

        val document = database.collection("users").document(Country.MEXICO).collection(Cities.QUERETARO).document(driver.id)
        document.set(driver)
            .addOnSuccessListener {
                uploadMultipleFile(driver.id,driver.images){ state->
                    when(state){
                        is UiState.Loading-> {}
                        is UiState.Failure->{
                            result.invoke(UiState.Failure(state.error))
                        }
                        is UiState.Success->{
                            val map = HashMap<String, String>()
                            map["latitude"] = "0.0"
                            map["longitude"] = "0.0"
                            map["status"] = "0"
                            realtimeDatabase.getReference("drivers").child(driver.id).setValue(map).addOnCompleteListener{task->
                                if (task.isSuccessful){
                                    result.invoke(UiState.Success("Success"))
                                }
                            }.addOnFailureListener {
                                result.invoke(UiState.Failure("Success"))
                            }

                        }
                    }
                }
                result.invoke(UiState.Success("User has been update successfully"))
            }.addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }
    fun uploadMultipleFile(id:String,fileUri: List<ImagesModel>, result: (UiState<String>) -> Unit){
        try {
            val listURL= ArrayList<ImagesModel>()
            fileUri.forEach { image->
                storageReference.child("ImagesDriver/$id/${image.name}_${Date.currentData()}")
                    .putFile(image.image!!).addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener {
                            val obj= ImagesModel(it,image.name)
                            listURL.add(obj)
                        }
                    }
            }
            database.collection(FireStoreCollection.DRIVERS).document(id).update("images",listURL).addOnCompleteListener {
                result.invoke(UiState.Success("Agregado con exito"))
            }.addOnFailureListener { error->
                result.invoke(UiState.Failure(error.localizedMessage))
            }
        }catch (e: Exception){
            result.invoke(UiState.Failure(e.localizedMessage))
        }
    }
    override suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storageReference.child(NOTE_IMAGES).child(image.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()

                    }
                }.awaitAll()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException){
            onResult.invoke(UiState.Failure(e.message))
        }catch (e: Exception){
            onResult.invoke(UiState.Failure(e.message))
        }
    }
    override suspend fun getDrivers(onResult: (UiState<List<Driver2>>) -> Unit) {
        try {
            val drivers=database.collection("users")
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .whereEqualTo("rol","driver")
                .whereEqualTo("route","")
                .get()
                .await()
                .toObjects(Driver2::class.java)
            Log.d("driversFragment","La lista $drivers")
            onResult.invoke(UiState.Success(drivers))
        }catch (e:Exception){
            onResult.invoke(UiState.Failure(e.localizedMessage))
        }
    }
    override suspend fun updateLocation(idUser: String,location:LocationEvent) {
        try {
            realtimeDatabase.getReference("drivers").child(idUser).setValue(location).await()
        }catch (e:Exception){
            e.localizedMessage?.let { Log.d("exepcion", it) }
        }
    }
    override suspend fun checkInOut(idUser:String,idRoute:String,isCheked:Boolean,onResult:(UiState<String>)-> Unit) {
        try {
            val list= mutableListOf("worker1","worker2")
            val data= hashMapOf<String,Any>()
            data["started"]=ServerValue.TIMESTAMP
            data["status"]=0
            data["workers"]= list

            realtimeDatabase
                .getReference("route_status")
                .child(idRoute)
                .setValue(data)
                .await()

        }catch (e:Exception){
            UiState.Failure(e.localizedMessage)
        }

    }
    override suspend fun getLocalDriver(onResult: (Driver2?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (user_str!!.isNotBlank()){
            val driver = gson.fromJson(user_str, Driver2::class.java)
            onResult.invoke(driver)
        }
    }
    override fun checkStatus(idRoute:String): Flow<UiState<Int>> {
        return realtimeDatabase
            .getReference("route_status")
            .child(idRoute)
            .snapshots
            .mapNotNull{
                UiState.Success(it.child("status").value.toString().toInt())
            }
            .catch { UiState.Failure(it.localizedMessage) }
    }

    override suspend fun getPoints(idRoute:String): List<Stop>? {
        return try {
            val list=database
                .collection(FireStoreCollection.ROUTES)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(idRoute)
                .collection(FireStoreDocumentSubcolection.STOPS)
                .get()
                .await()
                .toObjects(Stop::class.java)
            list
        }catch (e:Exception){
            null
        }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,null).apply()
        result.invoke()
    }

}