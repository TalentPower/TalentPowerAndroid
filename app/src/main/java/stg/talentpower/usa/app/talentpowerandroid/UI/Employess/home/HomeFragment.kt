package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.EmployessActivity
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.Adapters.AdapterRoutes
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.HomeViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.createDialog
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentHomeEmployessBinding

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeEmployessBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeEmployessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observers()

        //FirebaseMessaging.getInstance().subscribeToTopic("/topics/testMessaging")

        /*
        binding.btnAddCliente.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addClientFragment)

            /*
            val notificationData=JSONObject()
            val notifcationBody = JSONObject()
            val notification = JSONObject()

            try {
                notificationData.put("tokenTest","198AA9S8FA8S")
                notificationData.put("routeId","Amealco- Parque Qto.")
                notificationData.put("tokenCase",false)
                notifcationBody.put("title", "Ruta Amealco Qto iniciada")
                notifcationBody.put("message", "El conductor a iniciado la ruta")   //Enter your notification message
                notifcationBody.put("data",notificationData)
                notification.put("to", "/topics/testMessaging")
                notification.put("data", notifcationBody)
            } catch (e: JSONException) {
                Log.e("TAG", "onCreate: " + e.message)
            }

            model.makeNot(notification)

             */


            /*
            val topic = "/topics/testMessaging" //topic has to match what the receiver subscribed to

            val notificationData=JSONObject()
            val notification = JSONObject()
            val notifcationBody = JSONObject()

            try {
                notificationData.put("tokenTest","198AA9S8FA8S")
                notificationData.put("routeId","Amealco- Parque Qto.")
                notificationData.put("tokenCase",false)
                notifcationBody.put("title", "Ruta Amealco Qto iniciada")
                notifcationBody.put("message", "El conductor a iniciado la ruta")   //Enter your notification message
                notifcationBody.put("data",notificationData)
                notification.put("to", topic)
                notification.put("data", notifcationBody)
                Log.e("TAG", "try")
            } catch (e: JSONException) {
                Log.e("TAG", "onCreate: " + e.message)
            }

            sendNotification(notification)

             */

        }

         */

        model.getRoutes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        //val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        //nav.show()
        //val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivity)
        //toolbar.hide()
    }

    private fun observers(){
        model.routes.observe(requireActivity()){ listRoutes->
            when(listRoutes){
                is UiState.Failure->{}

                is UiState.Success->{
                    val itemAdapter=AdapterRoutes(listRoutes.data){ route ->
                        val bundle = bundleOf(
                            "name" to route.name,
                            "driver" to route.driver,
                            "client" to route.client,
                            "noRequWorkers" to route.noRequWorkers,
                            "noReclWorkers" to route.noReclWorkers,
                            "noRealWorkers" to route.noRealWorkers,
                            "idDriver" to route.idDriver,
                            "createdItem" to route.createdItem,
                            "status" to route.status
                        )
                        findNavController().navigate(R.id.action_navigation_home_to_routeDetailsFragment,bundle)
                    }
                    binding.rvRoutes.layoutManager = LinearLayoutManager(context)
                    binding.rvRoutes.adapter = itemAdapter
                }
                is UiState.Loading->{
                    Log.d("gettingRoutes","Loading")
                }
                else -> {}
            }
        }
    }
}