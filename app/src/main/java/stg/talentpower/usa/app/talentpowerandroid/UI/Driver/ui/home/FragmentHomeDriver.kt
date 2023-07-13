package stg.talentpower.usa.app.talentpowerandroid.UI.Driver.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.DistanceRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.EstimatedTimeToArrivalFormatter
import com.mapbox.navigation.ui.tripprogress.model.PercentDistanceTraveledFormatter
import com.mapbox.navigation.ui.tripprogress.model.TimeRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.TripProgressUpdateFormatter
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.LocationEvent
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.Util.LocationPermissionHelper
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.Util
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.round
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentHomeDriverBinding
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@AndroidEntryPoint
class FragmentHomeDriver : Fragment(){

    private var _binding: FragmentHomeDriverBinding? = null
    private val binding get() = _binding!!

    private val model:HomeViewModelDriver by viewModels()

    //private var service: Intent?=null
    //var id=""
    //private val database=FirebaseDatabase.getInstance().getReference("locations")


    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }



    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onCameraTrackingDismissed() {
        binding.mapViewDriverHome.gestures.removeOnMoveListener(onMoveListener)
    }


    private val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                Log.d("mapboxNavigation","onAttached")
                if (!mapboxNavigation.isRunningForegroundService()){
                    mapboxNavigation.registerRoutesObserver(model.routesObserver)
                    mapboxNavigation.registerLocationObserver(model.locationObserver)
                    mapboxNavigation.registerRouteProgressObserver(model.routeProgressObserver)
                    mapboxNavigation.registerVoiceInstructionsObserver(model.voiceInstructionsObserver)
                    mapboxNavigation.startTripSession()
                }

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                Log.d("mapboxNavigation","onDetached")
                mapboxNavigation.unregisterRoutesObserver(model.routesObserver)
                mapboxNavigation.unregisterLocationObserver(model.locationObserver)
                mapboxNavigation.unregisterRouteProgressObserver(model.routeProgressObserver)
                mapboxNavigation.unregisterVoiceInstructionsObserver(model.voiceInstructionsObserver)
            }
        },
        onInitialize = this::initNavigation
    )



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("lifecycFragment","onCreateView")
        _binding = FragmentHomeDriverBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lifecycFragment","onViewCreated")
        if (!model.firstLocationUpdateReceived){
            Log.d("mapboxNavigation","Voy a inicializar variables")

            val viewportDataSources = MapboxNavigationViewportDataSource(binding.mapViewDriverHome.getMapboxMap())
            model.setviewPort(viewportDataSources)

            val navigationCameras = NavigationCamera(
                binding.mapViewDriverHome.getMapboxMap(),
                binding.mapViewDriverHome.camera,
                viewportDataSources
            )
            model.setNavCamera(navigationCameras)


            binding.mapViewDriverHome.camera.addCameraAnimationsLifecycleListener(
                NavigationBasicGesturesHandler(model.navigationCamera)
            )

            model.navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
// shows/hide the recenter button depending on the camera state
                when (navigationCameraState) {
                    NavigationCameraState.TRANSITION_TO_FOLLOWING,
                    NavigationCameraState.FOLLOWING -> binding.recenter.visibility = View.INVISIBLE
                    NavigationCameraState.TRANSITION_TO_OVERVIEW,
                    NavigationCameraState.OVERVIEW,
                    NavigationCameraState.IDLE -> binding.recenter.visibility = View.VISIBLE
                }
            }

            // set the padding values depending on screen orientation and visible view layout
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                model.viewportDataSource.overviewPadding = landscapeOverviewPadding
            } else {
                model.viewportDataSource.overviewPadding = overviewPadding
            }
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                model.viewportDataSource.followingPadding = landscapeFollowingPadding
            } else {
                model.viewportDataSource.followingPadding = followingPadding
            }

            // make sure to use the same DistanceFormatterOptions across different features
            val distanceFormatterOptions = DistanceFormatterOptions.Builder(requireContext()).build()

// initialize maneuver api that feeds the data to the top banner maneuver view
            val maneuverApis = MapboxManeuverApi(
                MapboxDistanceFormatter(distanceFormatterOptions)
            )
            model.setManeuverApi(maneuverApis)

            val tripProgressApis = MapboxTripProgressApi(
                TripProgressUpdateFormatter.Builder(requireContext())
                    .distanceRemainingFormatter(
                        DistanceRemainingFormatter(distanceFormatterOptions)
                    )
                    .timeRemainingFormatter(
                        TimeRemainingFormatter(requireContext())
                    )
                    .percentRouteTraveledFormatter(
                        PercentDistanceTraveledFormatter()
                    )
                    .estimatedTimeToArrivalFormatter(
                        EstimatedTimeToArrivalFormatter(requireContext(), TimeFormat.NONE_SPECIFIED)
                    )
                    .build()
            )

            model.setTripProgressApi(tripProgressApis)


            val speechApis = MapboxSpeechApi(
                requireContext(),
                getString(R.string.mapbox_access_token),
                Locale.US.language
            )
            model.setSpechApi(speechApis)
            val voiceInstructionsPlayers = MapboxVoiceInstructionsPlayer(
                requireContext(),
                getString(R.string.mapbox_access_token),
                Locale.US.language
            )
            model.setVoiceInstructions(voiceInstructionsPlayers)

            val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(requireContext())
                .withRouteLineBelowLayerId("road-label-navigation")
                .build()
            val routeLineApis = MapboxRouteLineApi(mapboxRouteLineOptions)
            val routeLineViews = MapboxRouteLineView(mapboxRouteLineOptions)
            model.setRouteLineApis(routeLineApis)
            model.setRouteLineView(routeLineViews)

            val routeArrowOptions = RouteArrowOptions.Builder(requireContext()).build()
            val routeArrowViews = MapboxRouteArrowView(routeArrowOptions)
            model.setArrowView(routeArrowViews)
        }else{
            Log.d("mapboxNavigation","Continue el flujo")
        }


        // load map style
        binding.mapViewDriverHome.getMapboxMap().loadStyleUri(NavigationStyles.NAVIGATION_DAY_STYLE) {
            checkPermisos{
                if (it){
                    setupGesturesListener()
                    binding.mapViewDriverHome.location.apply {
                        setLocationProvider(model.navigationLocationProvider)
                        this.locationPuck = LocationPuck2D(
                            bearingImage = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.mapbox_user_puck_icon
                            )
                        )
                        enabled = true
                    }
                }
            }
        }

        //-------------------------------NO TOCAR--------------------//
        if (model.driver.value==null)model.getLocalDriver()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            if (model.status.value==null) model.checkStatus()
        }

        binding.apply {
            includeLayoutDriver.swInOut.setOnClickListener {
                model.checkInDriver(includeLayoutDriver.swInOut.isChecked)
            }
            includeLayoutDriver.btnStartEndRoute.setOnClickListener {
                //findRoute(Point.fromLngLat(-100.0088054,19.9164268,))
                model.getPoints()
            }
        }

        observers()
        /*
       service = Intent(requireActivity(),LocationService::class.java)
       val gson=Gson()
       val sharedPref=context?.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
       if (sharedPref!=null){
           val user_str = sharedPref.getString(SharedPrefConstants.USER_SESSION,null)
           id= gson.fromJson(user_str, Driver2::class.java).id
       }
        */
    }

    private fun findRoute(destination: Point) {
        val originLocation = model.navigationLocationProvider.lastLocation
        val originPoint = originLocation?.let { Point.fromLngLat(it.longitude, it.latitude) } ?: return

        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .applyLanguageAndVoiceUnitOptions(requireContext())
                .coordinatesList(listOf(originPoint, destination))
                .bearingsList(listOf(
                        Bearing.builder()
                            .angle(originLocation.bearing.toDouble())
                            .degrees(45.0)
                            .build(),
                        null)
                )
                .layersList(listOf(mapboxNavigation.getZLevel(), null))
                .build(),
            object : NavigationRouterCallback {

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) { }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) { }

                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: RouterOrigin) {
                    setRouteAndStartNavigation(routes)
                }
            }
        )
    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        mapboxNavigation.setNavigationRoutes(routes)

// show UI elements
        binding.soundButton.show()
        binding.routeOverview.show()
        //binding.recenter.show()
        model.navigationCamera.requestNavigationCameraToFollowing()

// move the camera to overview when new route is available
        //model.navigationCamera.requestNavigationCameraToOverview()
        lifecycleScope.launch{
            delay(5000)
            model.navigationCamera.requestNavigationCameraToFollowing()
        }
    }

    private fun clearRouteAndStopNavigation() {
// clear
        mapboxNavigation.setNavigationRoutes(listOf())

// stop simulation
        //mapboxReplayer.stop()

// hide UI elements
        binding.soundButton.hide()
        binding.maneuverView.hide()
        binding.routeOverview.hide()
        binding.tripProgressCard.hide()
    }

    private fun initNavigation() {
        MapboxNavigationApp.setup(
            NavigationOptions.Builder(requireContext())
                .accessToken(getString(R.string.mapbox_access_token))
                .build()
        )
    }

    private fun setupGesturesListener() {
        binding.mapViewDriverHome.gestures.addOnMoveListener(onMoveListener)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observers(){

        model.listPoint.observe(requireActivity()){list->
            if (list.isNotEmpty()){
                //findRoute(list.last().location)
            }
        }

        model.routesOserverData.observe(requireActivity()){routeUpdateResult->

        if (routeUpdateResult.navigationRoutes.isNotEmpty()) {
// generate route geometries asynchronously and render them
            model.routeLineApi.setNavigationRoutes(
                routeUpdateResult.navigationRoutes
            ) { value ->
                binding.mapViewDriverHome.getMapboxMap().getStyle()?.apply {
                    model.routeLineView.renderRouteDrawData(this, value)
                }
            }

// update the camera position to account for the new route
            model.viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
            model.viewportDataSource.evaluate()
        } else {
// remove the route line and route arrow from the map
            val style = binding.mapViewDriverHome.getMapboxMap().getStyle()
            if (style != null) {
                model.routeLineApi.clearRouteLine { value ->
                    model.routeLineView.renderClearRouteLineValue(
                        style,
                        value
                    )
                }
                model.routeArrowView.render(style, model.routeArrowApi.clearArrows())
            }

// remove the route reference from camera position evaluations
            model.viewportDataSource.clearRouteData()
            model.viewportDataSource.evaluate()
        }


        }

        model.voiceInstructionsObserverData.observe(requireActivity()){expected->
            expected.fold(
                { error ->
// play the instruction via fallback text-to-speech engine
                    model.voiceInstructionsPlayer.play(
                        error.fallback,
                        model.voiceInstructionsPlayerCallback
                    )
                },
                { value ->
// play the sound file from the external generator
                    model.voiceInstructionsPlayer.play(
                        value.announcement,
                        model.voiceInstructionsPlayerCallback
                    )
                }
            )
        }

        model.routeProgressObserverData.observe(requireActivity()){routeProgress->
            model.viewportDataSource.onRouteProgressChanged(routeProgress)
            model.viewportDataSource.evaluate()
            lifecycleScope.launch (Dispatchers.IO){
                val toTravelKm=(routeProgress.currentLegProgress!!.distanceRemaining/1000).toDouble().round(1)
                val arrivalHrs = TimeUnit.SECONDS.toHours(routeProgress.currentLegProgress!!.durationRemaining.toLong()).toInt()
                val arrivalMin= TimeUnit.SECONDS.toMinutes(routeProgress.currentLegProgress!!.durationRemaining.toLong()).toInt()
                Log.d("routeProgress","Original hrs :$arrivalHrs")
                Log.d("routeProgress","Original Min :$arrivalMin")

                if (arrivalHrs>0){
                    val suspMin=arrivalMin-arrivalHrs*60
                    Log.d("routeProgress","susp Min :$suspMin")
                    val estimatedHrs= stg.talentpower.usa.app.talentpowerandroid.Util.Date.currentDataObject()!!.hour+arrivalHrs
                    val estimatedMin= stg.talentpower.usa.app.talentpowerandroid.Util.Date.currentDataObject()!!.minute+suspMin

                    Log.d("routeProgress","Arriving aprox :$estimatedHrs : $estimatedMin")
                    if (suspMin>59){
                        estimatedHrs
                        estimatedMin-60
                    }
                    val time= Util.DateHelper.returnTimeCasted(estimatedHrs,estimatedMin)
                    Log.d("routeProgress","El time :$time")
                }else{
                    var estimatedHrs= stg.talentpower.usa.app.talentpowerandroid.Util.Date.currentDataObject()!!.hour
                    var estimatedMin= stg.talentpower.usa.app.talentpowerandroid.Util.Date.currentDataObject()!!.minute+arrivalMin
                    if (estimatedMin>59){
                        estimatedHrs -= 1
                        estimatedMin -= 60
                    }
                    val time= Util.DateHelper.returnTimeCasted(estimatedHrs,estimatedMin)
                    Log.d("routeProgress","El time :$time")
                }
            }
// draw the upcoming maneuver arrow on the map
            val style = binding.mapViewDriverHome.getMapboxMap().getStyle()
            if (style != null) {
                val maneuverArrowResult = model.routeArrowApi.addUpcomingManeuverArrow(routeProgress)
                model.routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
            }

// update top banner with maneuver instructions
            val maneuvers = model.maneuverApi.getManeuvers(routeProgress)
            maneuvers.fold(
                { error ->
                    Toast.makeText(
                        requireContext(),
                        error.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                {
                    binding.maneuverView.visibility = View.VISIBLE
                    binding.maneuverView.renderManeuvers(maneuvers)
                }
            )

// update bottom trip progress summary
            binding.tripProgressView.render(
                model.tripProgressApi.getTripProgress(routeProgress)
            )
            //Log.d("routeProgress",routeProgress.toString())

        }

        model.lastLocation.observe(requireActivity()){ location ->

        }

        model.driver.observe(requireActivity()){ driver->
            binding.apply {
                includeLayoutDriver.txtNameDriverHome.text=driver?.name
                Glide.with(requireActivity()) //1
                    .load(driver?.image)
                    .placeholder(R.drawable.ic_acount_24)
                    .error(R.drawable.ic_image_error_24)
                    .skipMemoryCache(true) //2
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                    .transform(CircleCrop()) //4
                    .into(includeLayoutDriver.imgProfileDriverHome)
            }
        }

        model.status.observe(requireActivity()){status->
            binding.apply {
                when(status){
                    is UiState.Success->{
                        when(status.data){
                            0->{
                                includeLayoutDriver.swInOut.isChecked=false
                                model.textStatusDriver(false)
                            }
                            1->{
                                includeLayoutDriver.swInOut.isChecked=true
                                model.textStatusDriver(true)
                            }
                            2->{
                                if (!includeLayoutDriver.swInOut.isChecked)includeLayoutDriver.swInOut.isChecked=true
                                model.textStatusDriver(true)
                            }
                            3->{
                                if (!includeLayoutDriver.swInOut.isChecked)includeLayoutDriver.swInOut.isChecked=true
                                model.textStatusDriver(true)
                            }
                        }
                    }
                    is UiState.Loading->{
                        Log.d("statusChecking","Loading")
                    }
                    is UiState.Failure->{

                        Log.d("statusChecking","On failure: ${status.error}")
                    }
                }
            }
        }

        model.textStatus.observe(requireActivity()){status->
            binding.apply {
                when(status){
                    true->{
                        includeLayoutDriver.txtStatusDriver.text="ON"
                    }
                    false->{
                        includeLayoutDriver.txtStatusDriver.text="OFF"
                    }
                }
            }

        }

        model.currentLocation.observe(requireActivity()){
            binding.includeLayoutDriver.txtNameDriverHome.text="Lat: ${it.latitude} Lng:${it.longitude}"
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPermisos(onResult: (Boolean)-> Unit){
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                myLocation()
                onResult.invoke(true)
                Log.d("permissionRequested","ACCESS_FINE_LOCATION")
            }
            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED->{
                myLocation()
                onResult.invoke(true)
                Log.d("permissionRequested","ACCESS_COARSE_LOCATION")

            }
            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED->{
                myLocation()
                onResult.invoke(true)
                Log.d("permissionRequested","ACCESS_BACKGROUND_LOCATION")

            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showAlertPermision()
                onResult.invoke(false)
            }
            else -> { locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                myLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                myLocation()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION,false)->{
                myLocation()
            }

            else -> {
            showAlertPermision()
        }
        }
    }
    @SuppressLint("MissingPermission")
    private fun myLocation(){
        Log.d("testLocation","MyLocation")
        model.lastLocation()
    }

    private fun showAlertPermision() {
        toast("Needs Location permision to work")
    }
    override fun onStart() {
        super.onStart()
        val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_drivers)
        nav.show()
        val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivityDrivers)
        toolbar.hide()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        Log.d("lifecycFragment","onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycFragment","onResume")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("lifecycFragment","onDestroyView")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("lifecycFragment","onSaveInstanceState")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("lifecycFragment","onDestroy")
        //context?.stopService(service)
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
        binding.mapViewDriverHome.gestures.removeOnMoveListener(onMoveListener)
        model.maneuverApi.cancel()
        model.routeLineApi.cancel()
        model.routeLineView.cancel()
        model.speechApi.cancel()
        model.voiceInstructionsPlayer.shutdown()

    }
    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
        /*val map= hashMapOf<String,String>()
        map["latitude"]=locationEvent.latitude.toString()
        map["longitude"]=locationEvent.longitude.toString()
        database.child(id).updateChildren(map as Map<String, Any>)
         */
        lifecycleScope.launch {
            //model.setlastLocation(location = locationEvent)
            model.setCurrentLocation(location = locationEvent)
        }
    }



}