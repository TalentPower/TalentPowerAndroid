package stg.talentpower.usa.app.talentpowerandroid.di

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import stg.talentpower.usa.app.talentpowerandroid.Util.SharedPrefConstants
import java.util.Locale
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context):
            FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder = Geocoder(context, Locale.getDefault())

    @Provides
    @Singleton
    fun provideVolley(@ApplicationContext context: Context): RequestQueue = Volley.newRequestQueue(context)
}