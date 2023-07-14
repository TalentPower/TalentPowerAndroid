package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.AddClientViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.createDialog
import stg.talentpower.usa.app.talentpowerandroid.Util.disable
import stg.talentpower.usa.app.talentpowerandroid.Util.enabled
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentAddClientBinding

@AndroidEntryPoint
class AddClientFragment : Fragment() {

    private lateinit var dialog: Dialog

    private var _binding: FragmentAddClientBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    private  val model: AddClientViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding= FragmentAddClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()

        lifecycleScope.launch {
            val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.google_map_client) as? SupportMapFragment
            map = mapFragment!!.awaitMap()
            checkPermisos()
        }

        binding.btnAddClient.setOnClickListener {
            val client=Client()
            client.name=binding.tfNameClient.text.toString()
            client.facturation=binding.tfClientWarranty.text.toString().toInt()
            model.registerClient(client = client)
        }
    }

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                myLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
            } else -> {
            showAlertPermision()
        }
        }
    }

    private fun showAlertPermision() {

    }

    private fun checkPermisos(){
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                myLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showAlertPermision()
            }
            else -> { locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }

    private fun idleCamera(){
        map.setOnCameraIdleListener {
            val obj= LatLng(
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude)
            val geo=GeoPoint(map.cameraPosition.target.latitude,map.cameraPosition.target.longitude)
            model.setLatlng(geo)
            model.geocodeLatLng(obj)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observers() {
        model.registerClient.observe(requireActivity()){ state->
            when(state){
                is UiState.Failure->{
                    dialog.dismiss()
                    state.error?.let { toast(it) }
                }
                is UiState.Loading->{
                    dialog = requireContext().createDialog(R.layout.loading_layout, true)
                    dialog.show()
                }
                is UiState.Success->{
                    dialog.dismiss()
                    toast(state.data)
                    clear()
                }

                else -> {}
            }
        }

        model.addres.observe(requireActivity()){
            if (it.equals("Sin direcciones")){
                binding.btnAddClient.disable()
                binding.txtAddresStop.text="Direccion:"
            }else {
                binding.btnAddClient.enabled()
                binding.txtAddresStop.text="Direccion: $it"
            }
            Log.d("addressClient",it)
        }

    }
    private fun clear(){
        binding.txtAddresStop.text=""
        binding.tfClientWarranty.text.clear()
        binding.tfNameClient.text.clear()
    }

    private fun myLocation(){
        model.lastLocation()
        model.lastLocation.observe(requireActivity()){data->
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(data,10f))
        }
        idleCamera()
    }

    override fun onStart() {
        super.onStart()
        //val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        //nav.hide()
        //val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivity)
        //toolbar.show()
    }
}