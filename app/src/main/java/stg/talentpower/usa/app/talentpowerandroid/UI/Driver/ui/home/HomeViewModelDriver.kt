package stg.talentpower.usa.app.talentpowerandroid.UI.Driver.ui.home

import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.LocationEvent
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.Repository.DriverRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.LocationRepository
import stg.talentpower.usa.app.talentpowerandroid.Repository.RouteRepository
import stg.talentpower.usa.app.talentpowerandroid.Util.Date
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.Util
import stg.talentpower.usa.app.talentpowerandroid.Util.round
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModelDriver @Inject constructor(
    private val driverRepository:DriverRepository,
    private val locationRepository:LocationRepository,
    private val routeRepository:RouteRepository
): ViewModel() {

    private val _lastLocation:MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }
    val lastLocation:LiveData<LatLng> get() =  _lastLocation

    private val _currentLocation:MutableLiveData<LocationEvent> by lazy {
        MutableLiveData<LocationEvent>()
    }
    val currentLocation: LiveData<LocationEvent> get() = _currentLocation

    private val _listPoints=MutableLiveData<List<Stop>>()
    val listPoint:LiveData<List<Stop>> get() = _listPoints

    private val _textStatus=MutableLiveData<Boolean>()
    val textStatus:LiveData<Boolean> get() = _textStatus

    private val _driver=MutableLiveData<Driver2?>()
    val driver:LiveData<Driver2?> get() = _driver

    private val _status=MutableLiveData<UiState<Int>>()
    val status:LiveData<UiState<Int>> get() = _status

    private val _endPoint= MutableLiveData<Point>()
    val endPoint: MutableLiveData<Point> get() = _endPoint


    fun getPoints(){
        viewModelScope.launch (Dispatchers.IO){
            _listPoints.postValue(driverRepository.getPoints(idRoute = driver.value!!.route))
        }
    }
    fun lastLocation() {
        viewModelScope.launch(Dispatchers.IO){
            _lastLocation.postValue(locationRepository.lastLocation())
        }
    }

    fun checkInDriver(isCheked:Boolean){
        //_textStatus.value=UiState.Loading
        viewModelScope.launch(Dispatchers.IO){
            driverRepository.checkInOut(idUser = driver.value!!.id, idRoute = driver.value!!.route, isCheked = isCheked) {
            }
        }
    }

    fun getLocalDriver(){
        viewModelScope.launch (Dispatchers.IO){
            driverRepository.getLocalDriver {driver->
                _driver.postValue(driver)
            }
        }
    }

    fun checkStatus(){
        _status.postValue(UiState.Loading)
        viewModelScope.launch (Dispatchers.IO){
            driverRepository.checkStatus(idRoute = driver.value!!.route).collect{ status->
                _status.postValue(status)
            }
        }
    }

    fun textStatusDriver(status:Boolean){
        _textStatus.value=status
    }

    fun setCurrentLocation(location:LocationEvent){
        _currentLocation.value=location
        viewModelScope.launch (Dispatchers.IO){
            driverRepository.updateLocation(idUser = driver.value!!.id, location = location)
        }
    }

    fun getEnd(){
        viewModelScope.launch (Dispatchers.IO){
            _endPoint.postValue(routeRepository.getEndPoint(idRoute = driver.value!!.route))
        }
    }

    //---------------------------Map Location-----------------
    private var _firstLocationUpdateReceived = false


    //--------------Routes Observers Api----------
    private val _routesObserver = RoutesObserver { routeUpdateResult ->
        _routesOserverData.value=routeUpdateResult
        /*
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

         */
    }
    val routesObserver:RoutesObserver get() = _routesObserver
    private val _routesOserverData:MutableLiveData<RoutesUpdatedResult> by lazy {
        MutableLiveData<RoutesUpdatedResult>()
    }
    val routesOserverData:LiveData<RoutesUpdatedResult> get() = _routesOserverData

    //-----------------------------Spech API Observers--------------------
    private val _voiceInstructionsObserver = VoiceInstructionsObserver { voiceInstructions ->
        _speechApi.generate(voiceInstructions, speechCallback)
    }
    val voiceInstructionsObserver:VoiceInstructionsObserver get() = _voiceInstructionsObserver
    private val speechCallback = MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
        _voiceInstructionsObserverData.value=expected
        /*
        expected.fold(
            { error ->
// play the instruction via fallback text-to-speech engine
                model.voiceInstructionsPlayer.play(
                    error.fallback,
                    voiceInstructionsPlayerCallback
                )
            },
            { value ->
// play the sound file from the external generator
                model.voiceInstructionsPlayer.play(
                    value.announcement,
                    voiceInstructionsPlayerCallback
                )
            }
        )

         */
    }
    private val _voiceInstructionsObserverData:MutableLiveData<Expected<SpeechError,SpeechValue>> by lazy {
        MutableLiveData<Expected<SpeechError,SpeechValue>>()
    }
    val voiceInstructionsObserverData:MutableLiveData<Expected<SpeechError,SpeechValue>> get() = _voiceInstructionsObserverData

    //---------------- Voice Api Observers---------
    private val _voiceInstructionsPlayerCallback = MapboxNavigationConsumer<SpeechAnnouncement> { value ->
// remove already consumed file to free-up space
        _speechApi.clean(value)
    }
    val voiceInstructionsPlayerCallback:MapboxNavigationConsumer<SpeechAnnouncement> get() = _voiceInstructionsPlayerCallback

   //-------------RouteProgresOservers-------
    private val _routeProgressObserver = RouteProgressObserver { routeProgress ->
       _routeProgressObserverData.value=routeProgress
// update the camera position to account for the progressed fragment of the route
        /*
        model.viewportDataSource.onRouteProgressChanged(routeProgress)
        model.viewportDataSource.evaluate()
        lifecycleScope.launch (Dispatchers.IO){
            val toTravelKm=(routeProgress.currentLegProgress!!.distanceRemaining/1000).toDouble().round(1)
            val arrivalHrs = TimeUnit.SECONDS.toHours(routeProgress.currentLegProgress!!.durationRemaining.toLong()).toInt()
            val arrivalMin= TimeUnit.SECONDS.toMinutes(routeProgress.currentLegProgress!!.durationRemaining.toLong()).toInt()

            if (arrivalHrs!=0){
                val suspMin=arrivalMin-arrivalHrs*60
                val estimatedHrs= Date.currentDataObject()!!.hour+arrivalHrs
                val estimatedMin= Date.currentDataObject()!!.minute+suspMin
                if (suspMin>59){
                    estimatedHrs+1
                    estimatedMin-60
                }
                val time= Util.DateHelper.returnTimeCasted(estimatedHrs,estimatedMin)
                Log.d("routeProgress","El time :$time")
            }else{
                val estimatedHrs= Date.currentDataObject()!!.hour
                val estimatedMin= Date.currentDataObject()!!.minute+arrivalMin
                if (estimatedMin>59){
                    estimatedHrs+1
                    estimatedMin-60
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

         */
    }
    val routeProgressObserver:RouteProgressObserver get() = _routeProgressObserver

    private val _routeProgressObserverData:MutableLiveData<RouteProgress> by lazy {
        MutableLiveData<RouteProgress>()
    }
    val routeProgressObserverData:MutableLiveData<RouteProgress> get() = _routeProgressObserverData




    val firstLocationUpdateReceived get() = _firstLocationUpdateReceived

    private val _navigationLocationProvider = NavigationLocationProvider()
    val navigationLocationProvider:NavigationLocationProvider get() = _navigationLocationProvider
    private lateinit var _navigationCamera: NavigationCamera
    val navigationCamera:NavigationCamera get() = _navigationCamera
    private lateinit var _viewportDataSource: MapboxNavigationViewportDataSource
    val viewportDataSource:MapboxNavigationViewportDataSource get() = _viewportDataSource

    private val _locationObserver = object : LocationObserver {
        override fun onNewRawLocation(rawLocation: Location) { }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            Log.d("routeProgress","onNewLocationMatcherResult")
            val enhancedLocation = locationMatcherResult.enhancedLocation
// update location puck's position on the map
            _navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,)
           // setCurrentLocation(LocationEvent(enhancedLocation.latitude,enhancedLocation.longitude))
// update camera position to account for new location
            _viewportDataSource.onLocationChanged(enhancedLocation)
            _viewportDataSource.evaluate()
// if this is the first location update the activity has received,
// it's best to immediately move the camera to the current user location
            if (!firstLocationUpdateReceived) {
                _firstLocationUpdateReceived = true
                _navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(1) // instant transition
                        .build())
            }
        }
    }
    val locationObserver: LocationObserver get() = _locationObserver

    private lateinit var _maneuverApi: MapboxManeuverApi
    val maneuverApi: MapboxManeuverApi get() = _maneuverApi
    private lateinit var _tripProgressApi: MapboxTripProgressApi
    val tripProgressApi: MapboxTripProgressApi get() = _tripProgressApi
    private lateinit var _routeLineApi: MapboxRouteLineApi
    val routeLineApi: MapboxRouteLineApi get() = _routeLineApi
    private lateinit var _routeLineView: MapboxRouteLineView
    val routeLineView: MapboxRouteLineView get() = _routeLineView

    private val _routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()
    val routeArrowApi: MapboxRouteArrowApi get() = _routeArrowApi
    private lateinit var _routeArrowView: MapboxRouteArrowView
    val routeArrowView: MapboxRouteArrowView get() = _routeArrowView

    private lateinit var _speechApi: MapboxSpeechApi
    val speechApi:MapboxSpeechApi get() = _speechApi
    private lateinit var _voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer
    val voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer get() = _voiceInstructionsPlayer

    fun setNavCamera(camera:NavigationCamera){
        _navigationCamera=camera
    }
    fun setviewPort(view:MapboxNavigationViewportDataSource){
        _viewportDataSource=view
    }
    fun setManeuverApi(api:MapboxManeuverApi){
        _maneuverApi=api
    }
    fun setTripProgressApi(api:MapboxTripProgressApi){
        _tripProgressApi=api
    }
    fun setRouteLineApis(lineApi: MapboxRouteLineApi){
        _routeLineApi=lineApi

    }
    fun setRouteLineView(view: MapboxRouteLineView ){
        _routeLineView=view

    }
    fun setArrowView(arrowView:MapboxRouteArrowView){
        _routeArrowView=arrowView
    }
    fun setSpechApi(spech:MapboxSpeechApi){
        _speechApi=spech
    }
    fun setVoiceInstructions(voice:MapboxVoiceInstructionsPlayer){
        _voiceInstructionsPlayer=voice
    }



}