package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.Model.Route
import stg.talentpower.usa.app.talentpowerandroid.Model.Worker
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.Adapters.AdapterWorkers
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.RouteDetailsViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.createDialog
import stg.talentpower.usa.app.talentpowerandroid.Util.disable
import stg.talentpower.usa.app.talentpowerandroid.Util.enabled
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentRouteDetailsBinding


@AndroidEntryPoint
class RouteDetailsFragment : Fragment() {

    private val model: RouteDetailsViewModel by viewModels()

    private var _binding: FragmentRouteDetailsBinding?=null
    private val binding get() = _binding!!

    private var route = Route()
    private lateinit var dialog: Dialog
    private lateinit var lists: List<Worker>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding= FragmentRouteDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgs()

        model.getRouteWorkers(route.name)

        binding.btnAddWorker.setOnClickListener {
            model.getStops(route.name)
            var selectedRoute=""
            dialog = requireContext().createDialog(R.layout.add_worker_dialog, true)
            val tfName=dialog.findViewById<TextView>(R.id.tfNameWorkerDialog)
            val tfPhone=dialog.findViewById<TextView>(R.id.tfPhoneWorkerDialog)
            val tfRFC=dialog.findViewById<TextView>(R.id.tfNameRFCDialog)
            val btnSaver=dialog.findViewById<Button>(R.id.btnAddWorkerDialog)
            val spDriver=dialog.findViewById<Spinner>(R.id.spStopsDialog)

            model.stops.observe(requireActivity()){data->
                when(data){
                    is UiState.Success->{
                        val list= mutableListOf<String>()
                        list.add("Seleccione una ruta")
                        data.data.forEach {
                            list.add(it.name)
                        }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spDriver.adapter=adapter
                    }
                    is UiState.Loading->{

                    }
                    is UiState.Failure->{

                    }

                    else -> {}
                }
            }

            spDriver.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedRoute= spDriver.selectedItem.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

            btnSaver.setOnClickListener {
                val obj=Worker()
                obj.name=tfName.text.toString()
                obj.phone=tfPhone.text.toString()
                obj.rfc=tfRFC.text.toString()
                obj.asignedRoute=route.name
                obj.asignedStop=selectedRoute
                obj.asignedClient=route.client
                model.addWorker(obj)
            }
            dialog.show()
        }

        model.realtimeWorkers.observe(requireActivity()){list->
            when(list){
                is UiState.Failure->{

                }
                is UiState.Success->{
                    if (list.data.isNotEmpty()){
                        val itemAdapter=AdapterWorkers(list.data){
                            Log.d("OnclickListenerUser","Data: $it")
                        }
                        binding.rvWorkers.layoutManager = LinearLayoutManager(context)
                        binding.rvWorkers.adapter = itemAdapter
                        lists= list.data
                        binding.txtRecl.text="Asientos Reclutados: "+list.data.size.toString()
                    }else{
                        binding.rvWorkers.adapter = null
                        binding.txtRecl.text="Asientos Reclutados: 0"
                    }
                }
                is UiState.Loading->{

                }
                else -> {}
            }
        }

        model.addWorker.observe(requireActivity()){ worker->
            if (worker!=null){
                when(worker){
                    is UiState.Loading->TODO()

                    is UiState.Failure->{
                        if(model.stops.hasObservers()) model.stops.removeObservers(requireActivity())
                        toast(worker.error)
                        dialog.dismiss()
                    }
                    is UiState.Success->{
                        if(model.stops.hasObservers()) model.stops.removeObservers(requireActivity())
                        toast(worker.data)
                        dialog.dismiss()
                        model.setNullWorker()
                    }
                }
            }
        }

        binding.btnManageMapRoute.setOnClickListener {
            val bundle = bundleOf(
                "name" to route.name,
                "client" to route.client,
                "driver" to route.driver,
                "startLat" to route.startPoint!!.latitude,
                "startLng" to route.startPoint!!.longitude,
                "endLat" to route.endPoint!!.latitude,
                "endLng" to route.endPoint!!.longitude,
            )
            findNavController().navigate(R.id.action_routeDetailsFragment_to_editRouteFragment,bundle)
        }

        binding.btnClearList.setOnClickListener {
            val temList= mutableListOf<Worker>()
            lists.forEach { worker->
                if (worker.workedDays==0)
                    temList.add(worker)
            }
            val num=route.noReclWorkers-temList.size
            binding.txtRecl.text=num.toString()
            model.clearList(route.name,num,temList.toList())
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.post {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    private fun getArgs() {
        if (arguments?.containsKey("name")!!){
            route.name= arguments?.getString("name")!!
            route.driver=arguments?.getString("driver")!!
            route.client=arguments?.getString("client")!!
            route.noRequWorkers=arguments?.getInt("noRequWorkers")!!
            route.noReclWorkers=arguments?.getInt("noReclWorkers")!!
            route.noRealWorkers=arguments?.getInt("noRealWorkers")!!
            route.idDriver= arguments?.getString("idDriver")!!
            route.status= arguments?.getString("status")!!
            route.startPoint= GeoPoint(requireArguments().getDouble("startLat"), requireArguments().getDouble("startLng"))
            route.endPoint= GeoPoint(requireArguments().getDouble("endLat"), requireArguments().getDouble("endLng"))

            binding.txtDriverRouteDetails.text="Conductor: ${route.driver}"
            binding.txtClientRouteDetails.text="Cliente:${route.client}"
            binding.txtNameRouteDetails.text= "Ruta: ${route.name}"
            binding.btnSeeRouteStatus.text="Status: ${route.status}"
            binding.txtReal.text="Asientos Ccupados: ${route.noRealWorkers}"
            binding.txtRequ.text="Asientos Solicitados: ${route.noRequWorkers}"
            binding.txtRecl.text="Asientos reclutados: ${route.noReclWorkers}"

            if (route.status == "INICIADA"){
                binding.btnSeeRouteStatus.enabled()
            }else binding.btnSeeRouteStatus.disable()
        }
    }

    override fun onStart() {
        super.onStart()
        val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.hide()
        val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivity)
        toolbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}