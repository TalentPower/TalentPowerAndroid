package stg.talentpower.usa.app.talentpowerandroid.UI.Driver

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.databinding.ActivityDriverBinding

class DriverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverBinding
    private lateinit var navController: NavController
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_driver) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolBarActivityDrivers)
        mAppBarConfiguration= AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController,mAppBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp()
    }
}