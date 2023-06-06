package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.R
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
    private lateinit var dialog: Dialog

    private val model:HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeEmployessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddCliente.setOnClickListener {
            dialog = requireContext().createDialog(R.layout.add_client_layout, true)

            val button = dialog.findViewById<MaterialButton>(R.id.btnAddClient)
            val tfName = dialog.findViewById<EditText>(R.id.tfNameClient)
            val tfAddress = dialog.findViewById<EditText>(R.id.tfAddressClient)
            val tfPhone = dialog.findViewById<EditText>(R.id.tfPhoneClient)
            val tfEmail = dialog.findViewById<EditText>(R.id.tfEmailClient)
            val tfPassword = dialog.findViewById<EditText>(R.id.tfPasswordClient)
            button.setOnClickListener {

                if (tfName.text.toString().isEmpty()){
                    tfName.error="Favor de completar todos los campos"
                    return@setOnClickListener
                }
                if (tfAddress.text.toString().isEmpty()){
                    tfAddress.error="Favor de completar todos los campos"
                    return@setOnClickListener
                }
                if (tfPhone.text.toString().isEmpty()) {
                    tfPhone.error="Favor de completar todos los campos"
                    return@setOnClickListener

                }
                if (tfEmail.text.toString().isEmpty()) {
                    tfEmail.error="Favor de completar todos los campos"
                    return@setOnClickListener

                }
                if (tfPassword.text.toString().isEmpty()) {
                    tfPassword.error="Favor de completar todos los campos"
                    return@setOnClickListener

                }
                //dialog.dismiss()
                val obj= Client(
                    "",
                    Name=tfName.text.toString(),
                    Ubicacion = tfAddress.text.toString(),
                    Phone = tfPhone.text.toString(),
                    "",
                    Email = tfEmail.text.toString(),
                    Password = tfEmail.text.toString(),
                )
                dialog.dismiss()

                model.registerClient(
                    email = tfEmail.text.toString(),
                    password = tfPassword.text.toString()
                    ,client = obj)
            }
            dialog.show()
        }

        model.register.observe(requireActivity()){ state->
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

                }
            }

        }

        binding.btnAddDriver.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addDriverFragment)
        }

        binding.btnAddRoute.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_createRouteFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.show()
        val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivity)
        toolbar.hide()
    }
}