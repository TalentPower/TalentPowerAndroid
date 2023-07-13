package stg.talentpower.usa.app.talentpowerandroid.UI.Driver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.databinding.ActivityDriverBinding

@AndroidEntryPoint
class DriverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverBinding
    private lateinit var navController: NavController
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navViewDrivers
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_driver) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolBarActivityDrivers)
        mAppBarConfiguration= AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController,mAppBarConfiguration)
        navView.setupWithNavController(navController)

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Location",
            NotificationManager.IMPORTANCE_HIGH,
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "location"
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp()
    }
}