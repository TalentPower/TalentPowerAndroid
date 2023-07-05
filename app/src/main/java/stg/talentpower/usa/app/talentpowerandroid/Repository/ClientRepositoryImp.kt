package stg.talentpower.usa.app.talentpowerandroid.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.EmployeeClient
import stg.talentpower.usa.app.talentpowerandroid.Util.Cities
import stg.talentpower.usa.app.talentpowerandroid.Util.Country
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ClientRepositoryImp (
    val auth: FirebaseAuth,
    val database: FirebaseFirestore): ClientRepository{
    override suspend fun registerClient(client: Client, onResult: (UiState<String>) -> Unit) {
        /*
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                client.id=it.result.user?.uid ?: ""
                addClient(client){state ->
                    when(state){
                        is UiState.Success->{
                            result.invoke(UiState.Success("Client register successfully!"))
                        }
                        is UiState.Failure->{
                            result.invoke(UiState.Failure(state.error))
                        }
                        is UiState.Loading->{

                        }

                        else -> {}
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
                result.invoke(UiState.Failure(it.localizedMessage))
            }

         */
/*
        val document = database.collection(FireStoreCollection.CLIENTS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(client.name)
        document.set(client).addOnSuccessListener {
            result.invoke(UiState.Success("CLient has been registred successfully"))
        }.addOnFailureListener {
            result.invoke(UiState.Failure(it.localizedMessage))
        }

 */

        try {
            database.collection(FireStoreCollection.CLIENTS)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .document(client.name)
                .set(client)
                .await()

            onResult.invoke(UiState.Success("CLient has been registred successfully"))
        }catch (e:Exception){
            onResult.invoke(UiState.Failure(e.localizedMessage))
        }

    }

    override fun addClient(client: EmployeeClient, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.EMPLOYEES_CLIENT)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(client.id)
        document.set(client).addOnSuccessListener {
            result.invoke(UiState.Success("User has been update successfully"))
        }.addOnFailureListener {
            result.invoke(UiState.Failure(it.localizedMessage))
        }
    }

    override fun deleteClient(uid: String, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun getClients(result: (UiState<List<Client>>) -> Unit) {
        try {
            val clients=database.collection(FireStoreCollection.CLIENTS)
                .document(Country.MEXICO)
                .collection(Cities.QUERETARO)
                .get()
                .await()
                .toObjects(Client::class.java)
            result.invoke(UiState.Success(clients))
        }catch (e:Exception){
            result.invoke(UiState.Failure(e.localizedMessage))
        }

    }

    override fun getClientAreas(nameClient: String, onResult: (UiState<List<String>>) -> Unit) {
        runBlocking {
            val list=getListAreas(nameClient)
            if (list.isNotEmpty()){
                onResult.invoke(UiState.Success(list))
            }else onResult.invoke(UiState.Failure(list.toString()))
        }
    }

    override fun registerEmployeeClient(email: String, password: String, userClient: EmployeeClient, result: (UiState<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                userClient.id=it.result.user?.uid ?: ""
                addClient(userClient){state ->
                    when(state){
                        is UiState.Success->{
                            result.invoke(UiState.Success("User register successfully!"))
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
            result.invoke(UiState.Failure(it.localizedMessage))
        }
    }

    private suspend fun getListAreas(nameClient:String):List<String> = suspendCancellableCoroutine { continuation->
        database.collection(FireStoreCollection.CLIENTS)
            .document(Country.MEXICO)
            .collection(Cities.QUERETARO)
            .document(nameClient)
            .get()
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    val doc=task.result
                    val client = doc.toObject(Client::class.java)
                    client?.areas?.let {
                        continuation.resume(it)
                    }
                }
            }
            .addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }
}