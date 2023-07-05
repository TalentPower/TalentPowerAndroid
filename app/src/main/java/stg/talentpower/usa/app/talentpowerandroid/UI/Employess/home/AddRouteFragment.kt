package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.ktx.addMarker
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.AddRouteViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.RouteStatus
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentAddRouteBinding


@AndroidEntryPoint
class AddRouteFragment : Fragment() {

    private val model: AddRouteViewModel by viewModels ()

    private var _binding:FragmentAddRouteBinding?=null
    val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var markerEnd:Marker
    private lateinit var markerStart:Marker

    private lateinit var driver:Driver2
    private lateinit var client:Client
    private val route = Route()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding= FragmentAddRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("startCoord"){ _, bundle ->
            val stop= Stop(
                bundle.getString("startName",""),
                bundle.getString("startAddress",""),
                GeoPoint(bundle.getDouble("startCoordLat"),bundle.getDouble("startCoordLng")),
                bundle.getString("startTime","")
            )
            Log.d("markerFragment",stop.toString())
            model.setStart(stop)
            route.timeStart= bundle.getString("startTime")!!
        }

        setFragmentResultListener("endCoord"){ _, bundle ->
            model.setEnd(
                LatLng(bundle.getDouble("endCoordLat")
                    ,bundle.getDouble("endCoordLng")
                )
            )
            route.timeEnd= bundle.getString("endTime")!!
        }

        mapView=binding.mapViewCreateRoute
        binding.mapViewCreateRoute.onCreate(savedInstanceState)

        binding.mapViewCreateRoute.getMapAsync { gMap->
            googleMap=gMap
            val latLng = LatLng(20.707879, -100.447887)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
            googleMap.moveCamera(cameraUpdate)
            observers()
            return@getMapAsync
        }

        binding.btnsetEndRoute.setOnClickListener {
            val args= bundleOf(
                "intent" to "endRoute"
            )
            findNavController().navigate(R.id.action_addRouteFragment_to_startEndStopsFragment,args)
        }

        binding.btnsetStartRoute.setOnClickListener {
            val args= bundleOf(
                "intent" to "startRoute"
            )
            findNavController().navigate(R.id.action_addRouteFragment_to_startEndStopsFragment,args)
        }

        binding.spDriver.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 driver= binding.spDriver.selectedItem as Driver2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        binding.spClient.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 client= binding.spClient.selectedItem as Client
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        if (model.listDrivers.value==null)model.getDrivers()
        if (model.listClients.value==null)model.getCLients()

        binding.btnSaveRoute.setOnClickListener {
            val end=GeoPoint(model.end.value!!.latitude,model.end.value!!.longitude)
            route.name=binding.tfNameRoute.text.toString()
            route.driver=driver.name
            route.idDriver=driver.id
            route.client=client.name
            route.noRequWorkers=binding.tfRequWorkersRoute.text.toString().toInt()
            route.startPoint=model.start.value!!.location
            route.endPoint=end
            route.status=RouteStatus.CREADA
            model.createRoute(route, model.start.value!!)
        }

    }

    private fun observers() {
        model.start.observe(requireActivity()){
            if (it!=null) {
                if (::markerStart.isInitialized) markerStart.remove()
                markerStart= googleMap.addMarker {
                    position(LatLng(it.location!!.latitude,it.location!!.longitude))
                    title("start")
                }!!
            }
        }

        model.end.observe(requireActivity()){
            if (it!=null) {
                if (::markerEnd.isInitialized) markerEnd.remove()
                markerEnd= googleMap.addMarker {
                    position(it)
                    title("start")
                }!!
            }
        }

        model.listDrivers.observe(requireActivity()){state->
            when(state){
                is UiState.Failure->{

                }

                is UiState.Loading->{

                }

                is UiState.Success->{
                    Log.d("driversFragment",state.data.toString())
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, state.data)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spDriver.adapter=adapter
                }
                else -> {}
            }

        }

        model.listClients.observe(requireActivity()){state->
            when(state){
                is UiState.Failure->{

                }
                is UiState.Loading->{

                }
                is UiState.Success->{
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, state.data)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spClient.adapter=adapter
                }
                else -> {}
            }
        }

        model.registerRoute.observe(requireActivity()){state->
            when(state){
                is UiState.Success->{
                    toast(state.data)
                    driver.route=binding.tfNameRoute.text.toString()
                    model.updateDriver(driver)
                    if (view!=null){
                        view?.post {
                            findNavController().popBackStack()
                        }
                    }
                }

                is UiState.Failure->{
                    toast(state.error)
                }

                is UiState.Loading->{

                }

                else -> {}
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.hide()
        val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivity)
        toolbar.show()
    }

}