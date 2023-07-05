package stg.talentpower.usa.app.talentpowerandroid.UI.Employess

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.Worker.MyWorker
import stg.talentpower.usa.app.talentpowerandroid.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class EmployessActivity : AppCompatActivity() {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create Network constraint
        startWork()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_employess) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolBarActivity)
        mAppBarConfiguration= AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController,mAppBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp()
    }

    fun createConstraints() = Constraints.Builder()
        .setRequiresCharging(true)
        .setRequiresBatteryNotLow(true)
        .build()

    fun createWorkRequest(data: Data) = PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.SECONDS)
        .setInputData(data)
        .setConstraints(createConstraints())
        //.setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MILLISECONDS)
        .build()

    fun startWork() {
        /*
        val work = createWorkRequest(Data.EMPTY)
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork("Sleep work",
            ExistingPeriodicWorkPolicy.UPDATE, work)

         */
        val repeatedReq = PeriodicWorkRequest.Builder(MyWorker::class.java,15,TimeUnit.MINUTES).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("YOURUNIQUENAME", ExistingPeriodicWorkPolicy.UPDATE, repeatedReq)
    }
}