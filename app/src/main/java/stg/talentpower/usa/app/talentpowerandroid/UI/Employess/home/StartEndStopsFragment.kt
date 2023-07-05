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
import stg.talentpower.usa.app.talentpowerandroid.databinding.StartEndStopsFragmentBinding

@AndroidEntryPoint
class StartEndStopsFragment : Fragment() {

    private var _binding: StartEndStopsFragmentBinding?=null
    val binding get() = _binding!!

    private lateinit var map: GoogleMap

    private var intent=""

    private val model: EditRouteViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= StartEndStopsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intent= arguments?.getString("intent")!!

        when(intent){
            "endRoute"->{
                binding.tfNameStopStartStop.hide()
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
            val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.google_map_start_stop) as? SupportMapFragment
            map = mapFragment!!.awaitMap()
            checkPermisos()
            map.setOnCameraIdleListener {
                val obj= LatLng(
                    map.cameraPosition.target.latitude,
                    map.cameraPosition.target.longitude)
                model.geocodeLatLng(obj)
            }
        }

        model.addres.observe(requireActivity()){
            binding.txtAddresStop.text=it
        }

        model.latLng.observe(requireActivity()){
            binding.txtLatStopStartStop.text= it.latitude.round(7).toString()
            binding.txtLnlStopStartStop.text=it.longitude.round(7).toString()
        }

        binding.btnSaveStopStartStop.setOnClickListener {
            when(intent){
                "startRoute"->{
                    model.latLng.value?.let { data ->
                        setFragmentResult("startCoord", bundleOf(
                            "startCoordLat" to data.latitude,
                            "startCoordLng" to data.longitude,
                            "startTime" to binding.btnHoursStartStop.text.toString(),
                            "startAddress" to model.addres.value,
                            "startName" to binding.tfNameStopStartStop.text.toString())
                        )
                        view.post {
                            findNavController().popBackStack()
                        }

                    }
                }
                "endRoute"->{
                    model.latLng.value?.let { data ->
                        setFragmentResult("endCoord", bundleOf(
                            "endCoordLat" to data.latitude,
                            "endCoordLng" to data.longitude,
                            "endTime" to binding.btnHoursStartStop.text.toString(),
                            "endAddress" to model.addres.value
                        )
                        )
                        view.post {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
        binding.btnHoursStartStop.setOnClickListener {
            val mTimePicker= TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                binding.btnHoursStartStop.text = TimePickerHelper.returnTime(hourOfDay,minute)
            }, TimePickerHelper.hour, TimePickerHelper.minute, false)
            mTimePicker.show()
        }
    }


    private fun myLocation(){
        model.lastLocation()
        model.lastLocation.observe(requireActivity()){data->
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