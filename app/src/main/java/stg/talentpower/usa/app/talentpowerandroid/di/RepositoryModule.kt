package stg.talentpower.usa.app.talentpowerandroid.di

import android.content.SharedPreferences
import android.location.Geocoder
import android.location.LocationManager
import com.android.volley.RequestQueue
import com.google.android.gms.location.FusedLocationProviderClient
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
import stg.talentpower.usa.app.talentpowerandroid.Repository.LocationRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.LocationRepositoryImp
import stg.talentpower.usa.app.talentpowerandroid.Repository.NotificationRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.NotificationRepositoryImp
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepositoryImp
import stg.talentpower.usa.app.talentpowerandroid.Repository.WorkerRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.WorkerRepositoryImp
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

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
        realtimeDatabase: FirebaseDatabase,
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        storageReference: StorageReference,
        appPreferences: SharedPreferences,
        gson: Gson
    ) : DriverRepository{
        return DriverRepositoryImp(auth,database,storageReference,realtimeDatabase,appPreferences,gson)
    }

    @Provides
    @Singleton
    fun provideRoutehRepository(
        database: FirebaseFirestore,
        realtimeDatabase: FirebaseDatabase,
    ):RouteRepository{
        return RouteRepositoryImp(database,realtimeDatabase)
    }

    @Provides
    @Singleton
    fun providesLocationhRepository(
        location:FusedLocationProviderClient,
        locationManager:LocationManager,
        geocoder:Geocoder,
        database: FirebaseFirestore
    ): LocationRepository {
        return LocationRepositoryImp(location,locationManager,geocoder,database)
    }

    @Provides
    @Singleton
    fun providesWorkerhRepository(
        database: FirebaseFirestore
    ): WorkerRepository{
        return WorkerRepositoryImp(database)
    }

    @Provides
    @Singleton
    fun providesNotificationhRepository(
        database: FirebaseFirestore,
        volley: RequestQueue
    ):NotificationRepository{
        return NotificationRepositoryImp(database,volley)
    }




}