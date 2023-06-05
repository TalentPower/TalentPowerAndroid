package stg.talentpower.usa.app.talentpowerandroid.UI.Login.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.Model.Employess
import stg.talentpower.usa.app.talentpowerandroid.UI.Login.ViewModel.AuthViewModel
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

        binding.registerBtn.setOnClickListener {
            val subobj=Employess()
            subobj.Email=binding.tfEmail.text.toString()
            subobj.Name=binding.tfName.text.toString()
            subobj.Rol="Employess"
            subobj.Password=binding.tfPassword.text.toString()
            subobj.Phone=binding.tfPassword.text.toString()

            model.register(
                binding.tfEmail.text.toString(),
                binding.tfPassword.text.toString(),
                subobj
            )
        }

        model.register.observe(requireActivity()){ state->
            when(state){
                is UiState.Failure->{
                    toast(state.error)
                }
                is UiState.Success->{
                    binding.registerBtn.setText("Register")
                    binding.registerProgress.hide()
                    binding.tfEmail.text.clear()
                    binding.tfPhone.text.clear()
                    binding.tfName.text.clear()
                    binding.tfPassword.text.clear()
                    toast(state.data)
                    //findNavController().navigate(R.id.action_registerFragment_to_home_navigation)
                }
                is UiState.Loading->{
                    binding.registerBtn.setText("")
                    binding.registerProgress.show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}