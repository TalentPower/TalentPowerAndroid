package stg.talentpower.usa.app.talentpowerandroid.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import stg.talentpower.usa.app.talentpowerandroid.Repository.AuthRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.AuthRepositoryImp
import stg.talentpower.usa.app.talentpowerandroid.Repository.ClientRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.ClientRepositoryImp
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepositoryImp
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
/*
    @Provides
    @Singleton
    fun provideNoteRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): NoteRepository{
        return NoteRepositoryImp(database,storageReference)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        database: FirebaseDatabase
    ): TaskRepository{
        return TaskRepositoryImp(database)
    }
*/
    @Provides
    @Singleton
    fun provideAutghRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(auth,database,appPreferences,gson)
    }

    @Provides
    @Singleton
    fun provideClienthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth
    ): ClientRepository{
        return ClientRepositoryImp(auth,database)
    }

    @Provides
    @Singleton
    fun provideDriverhRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        storageReference: StorageReference
    ) : DriverRepository{
        return DriverRepositoryImp(auth,database,storageReference)
    }


}