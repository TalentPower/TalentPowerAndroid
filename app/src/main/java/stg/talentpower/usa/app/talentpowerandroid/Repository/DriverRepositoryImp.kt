package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver
import stg.talentpower.usa.app.talentpowerandroid.Model.ImagesModel
import stg.talentpower.usa.app.talentpowerandroid.Util.Date
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.FirebaseStorageConstants
import stg.talentpower.usa.app.talentpowerandroid.Util.FirebaseStorageConstants.NOTE_IMAGES
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

class DriverRepositoryImp (
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val storageReference: StorageReference) :DriverRepository{

    override fun registerDriver(
        email: String,
        password: String,
        driver: Driver,
        result: (UiState<String>) -> Unit){

        result.invoke(UiState.Loading)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                driver.id=it.result.user?.uid ?: ""
                updateDriverInfo(driver){state ->
                    when(state){
                        is UiState.Success->{
                            result.invoke(UiState.Success("Client register successfully!"))
                        }
                        is UiState.Failure->{
                            result.invoke(UiState.Failure(state.error))
                        }
                        is UiState.Loading->{

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
        }.addOnFailureListener {
            result.invoke(
                UiState.Failure(
                    it.localizedMessage
                )
            )
        }
    }

    override fun updateDriverInfo(driver: Driver, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.DRIVERS).document(driver.id)
        document.set(driver)
            .addOnSuccessListener {
                uploadMultipleFile(driver.id,driver.images){ state->
                    when(state){
                        is UiState.Loading->{

                        }
                        is UiState.Failure->{

                            result.invoke(UiState.Failure(state.error))
                        }
                        is UiState.Success->{

                            result.invoke(UiState.Success("Success"))


                        }

                    }

                }


                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    fun uploadMultipleFile(id:String,fileUri: List<ImagesModel>, result: (UiState<String>) -> Unit){

        try {
            val listURL= ArrayList<ImagesModel>()
            fileUri.forEach { image->
                storageReference.child("ImagesDriver/$id/${image.Name}_${Date.currentData()}")
                    .putFile(image.Image!!).addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener {
                            val obj= ImagesModel(it,image.Name)
                            listURL.add(obj)
                        }
                    }
            }

            val hashMap : HashMap<String, String> = HashMap()
            listURL.forEach { obj->
                hashMap["images.${obj.Name}"] = "${obj.Image}"
            }
            database.collection(FireStoreCollection.DRIVERS).document(id).update(hashMap as Map<String, String>).addOnCompleteListener {
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

}