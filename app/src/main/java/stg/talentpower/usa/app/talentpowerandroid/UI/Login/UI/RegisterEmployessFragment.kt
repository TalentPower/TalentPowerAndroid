package stg.talentpower.usa.app.talentpowerandroid.UI.Login.UI

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.Model.Employee
import stg.talentpower.usa.app.talentpowerandroid.UI.Login.ViewModel.AuthViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.EmployessAreas
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentRegisterEmployessBinding

@AndroidEntryPoint
class RegisterEmployessFragment : Fragment() {

    private var _binding: FragmentRegisterEmployessBinding? = null
    private val binding get() = _binding!!

    private val model: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterEmployessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var area=""

        binding.registerBtn.setOnClickListener {
            val subobj=Employee()
            subobj.name=binding.tfName.text.toString()
            subobj.area=area
            subobj.phone=binding.tfPhone.text.toString()

            model.register(
                binding.tfEmail.text.toString(),
                binding.tfPassword.text.toString(),
                subobj
            )
        }

        val productNameList:List<String> = listOf(
            "Seleccionar area",
            EmployessAreas.CONTABILIDAD,
            EmployessAreas.DIRECCION,
            EmployessAreas.JURIDICO,
            EmployessAreas.VENTAS,
            EmployessAreas.SISTEMAS,
            EmployessAreas.RECLUTAMIENTO
        )

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, productNameList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spArea.adapter=adapter

        binding.spArea.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                area= binding.spArea.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        model.register.observe(requireActivity()){ state->
            when(state){
                is UiState.Failure->{
                    clear()
                    toast(state.error)
                }
                is UiState.Success->{
                    clear()
                    toast(state.data)
                    //findNavController().navigate(R.id.action_registerFragment_to_home_navigation)

                }
                is UiState.Loading->{
                    binding.registerBtn.setText("")
                    binding.registerProgress.show()
                }

                else -> {}
            }
        }

    }

    fun clear(){
        binding.registerBtn.setText("Register")
        binding.registerProgress.hide()
        binding.tfEmail.text.clear()
        binding.tfPhone.text.clear()
        binding.tfName.text.clear()
        binding.tfPassword.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}