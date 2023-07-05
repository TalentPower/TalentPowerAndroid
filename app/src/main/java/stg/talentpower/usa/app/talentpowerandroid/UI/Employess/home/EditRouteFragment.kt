package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Stop
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.Adapters.AdapterStops
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.EditRouteViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentEditRouteBinding


@AndroidEntryPoint
class EditRouteFragment : Fragment() {

    private var _binding: FragmentEditRouteBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

    private val model: EditRouteViewModel by viewModels()

    private lateinit var toolBar:Toolbar

    var id=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentEditRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.post {
                    findNavController().popBackStack()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        toolBar=requireActivity().findViewById(R.id.toolBarActivity)

        id= arguments?.getString("name")!!
        toolBar.title = "Ruta: $id"

        model.setStart(LatLng(requireArguments().getDouble("startLat"), requireArguments().getDouble("startLng")))
        model.setEnd(LatLng(requireArguments().getDouble("endLat"), requireArguments().getDouble("endLng")))


        binding.txtCliente.text=arguments?.getString("driver")
        binding.txtChofer.text=arguments?.getString("client")

        mapView=binding.mapView
        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync { gMap->
            googleMap=gMap
            val latLng = LatLng(20.707879, -100.447887)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
            googleMap.moveCamera(cameraUpdate)
            observers()
            return@getMapAsync
        }

        setupMenu()

    }

    private fun observers() {
        model.stops.observe(requireActivity()){ list->
            when(list){
                is UiState.Loading->{

                }
                is UiState.Failure->{

                }
                is UiState.Success->{

                    if (list.data.isEmpty()){
                        setMarkers(null)
                        getdirections(null)
                        binding.rvStops.adapter = null
                    }else{
                        val itemAdapter= AdapterStops(list.data){ btn,obj->
                            when(btn.id){
                                R.id.btnImgEditStop->{

                                }

                                R.id.btnImgDeleteStop->{
                                    id.let {
                                        model.deleteStop(id,obj)
                                    }
                                }
                            }
                        }
                        binding.rvStops.layoutManager = LinearLayoutManager(context)
                        binding.rvStops.adapter = itemAdapter
                        setMarkers(list.data)
                        getdirections(list.data)
                    }

                }
                else -> {

                }
            }
        }
        model.stopStatus.observe(requireActivity()){ status->
            when(status){
                is UiState.Failure->{
                    toast(status.error)
                }
                is UiState.Success->{
                    toast(status.data)
                }
                is UiState.Loading->{

                }

                else -> {}
            }
        }

    }

    private fun getdirections(data: List<Stop>?) {
        val list= mutableListOf<LatLng>()
        data?.forEach { stop->
            val loc=LatLng(stop.location!!.latitude,stop.location!!.longitude)
            list.add(loc)
        }
        GoogleDirection.withServerKey(requireActivity().resources.getString(R.string.google_maps_key))
            .from(model.start.value!!)
            .and(list.toList())
            .to(model.end.value!!)
            .execute(onDirectionSuccess = { direction: Direction? ->
                    if(direction!!.isOK) {
                        direction.routeList[0].legList.forEach { list->
                            val directionPositionList = list.directionPoint
                            val polylineOptions = DirectionConverter.createPolyline(
                                requireContext(),
                                directionPositionList,
                                5,
                                Color.RED
                            )
                            googleMap.addPolyline(polylineOptions)
                        }

                    } else {
                        // Do something
                        Log.d("directions", "La direccion no se decodifico")


                    }
                },
                onDirectionFailure = { t: Throwable ->
                    // Do something
                    Log.d("directions", "Trowable"+t.toString())

                }
            )
    }

    private fun setMarkers(data: List<Stop>?) {

        lifecycleScope.launch {
            googleMap.clear()
            googleMap.addMarker {
                position(LatLng(model.start.value!!.latitude, model.start.value!!.longitude))
                title("Start")
            }
            googleMap.addMarker {
                position(LatLng(model.end.value!!.latitude, model.end.value!!.longitude))
                title("End")
            }
            data?.forEach { stop->
                googleMap.addMarker {
                    position(LatLng(stop.location!!.latitude, stop.location!!.longitude))
                    title(stop.name)
                }
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit_route, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.add_circle->{
                        val bundle = bundleOf(
                            "id" to id,
                            "intent" to "idRoute")
                        findNavController().navigate(R.id.action_editRouteFragment_to_addStopFragment,bundle)
                        true
                    }
                    else -> {false}
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        id.let { model.getStops(it) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        model.stopListener()
    }

}