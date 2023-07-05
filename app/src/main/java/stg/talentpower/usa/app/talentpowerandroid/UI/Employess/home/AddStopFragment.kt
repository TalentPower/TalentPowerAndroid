package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.EditRouteViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.TimePickerHelper
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.round
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentAddStopBinding

@AndroidEntryPoint
class AddStopFragment : Fragment() {

    private var _binding: FragmentAddStopBinding?=null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val routeModel:EditRouteViewModel by viewModels ()
    private var intent=""
    private var idRoute=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentAddStopBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intent= arguments?.getString("intent")!!

        when(intent){
            "idRoute"->{
                idRoute= arguments?.getString("id")!!
            }
            "endRoute"->{
                binding.tfNameStop.hide()
            }
            "startRoute"->{

            }
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.post {
                    findNavController().popBackStack()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        lifecycleScope.launch {
            val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
            map = mapFragment!!.awaitMap()
            checkPermisos()
            map.setOnCameraIdleListener {
                val obj= LatLng(
                    map.cameraPosition.target.latitude,
                    map.cameraPosition.target.longitude)
                routeModel.geocodeLatLng(obj)
            }
        }

        routeModel.addres.observe(requireActivity()){
            binding.txtAddresStop.text=it
        }

        routeModel.latLng.observe(requireActivity()){
            binding.txtLatStop.text= it.latitude.round(7).toString()
            binding.txtLnlStop.text=it.longitude.round(7).toString()
        }

        binding.btnSaveStop.setOnClickListener {
            when(intent){
                "idRoute"->{
                    routeModel.saveRoute(idRoute,binding.tfNameStop.text.toString(),binding.btnHours.text.toString())
                }

                "startRoute"->{
                    routeModel.latLng.value?.let { data ->
                        setFragmentResult("startCoord", bundleOf(
                            "startCoordLat" to data.latitude,
                            "startCoordLng" to data.longitude,
                            "startTime" to binding.btnHours.text.toString(),
                            "startAddress" to routeModel.addres.value,
                            "startName" to binding.tfNameStop.text.toString()

                        ))
                        findNavController().popBackStack()
                    }
                }

                "endRoute"->{
                    routeModel.latLng.value?.let { data ->
                        setFragmentResult("endCoord", bundleOf(
                            "endCoordLat" to data.latitude,
                            "endCoordLng" to data.longitude,
                            "endTime" to binding.btnHours.text.toString(),
                            "endAddress" to routeModel.addres.value
                        ))
                        findNavController().popBackStack()
                    }
                }
            }
        }

        binding.btnHours.setOnClickListener {
            val mTimePicker=TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                binding.btnHours.text = TimePickerHelper.returnTime(hourOfDay,minute)
            }, TimePickerHelper.hour, TimePickerHelper.minute, false)
            mTimePicker.show()
        }

        routeModel.registerRoute.observe(requireActivity()){state->
            when(state){
                is UiState.Loading->{

                }
                is UiState.Success->{
                    toast(state.data)
                    findNavController().navigateUp()
                }
                is UiState.Failure->{
                    toast(state.error)
                }
                else -> {}
            }
        }
    }

    private fun myLocation(){
        routeModel.lastLocation()
        routeModel.lastLocation.observe(requireActivity()){data->
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(data,10f))
        }
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

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                myLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            showAlertPermision()
        }
        }
    }

    private fun showAlertPermision() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}