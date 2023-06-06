package stg.talentpower.usa.app.talentpowerandroid.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Util.FireStoreCollection
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState

class ClientRepositoryImp (
    val auth: FirebaseAuth,
    val database: FirebaseFirestore): ClientRepository{
    override fun registerClient(
        email: String,
        password: String,
        client: Client,
        result: (UiState<String>) -> Unit
    ) {
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

    override fun addClient(client: Client, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.CLIENTS).document(client.id)
        document.set(client)
            .addOnSuccessListener {
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

    override fun deleteClient(uid: String, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }
}