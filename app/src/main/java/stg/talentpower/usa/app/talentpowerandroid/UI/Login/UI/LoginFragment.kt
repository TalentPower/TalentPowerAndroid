package stg.talentpower.usa.app.talentpowerandroid.UI.Login.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Login.ViewModel.AuthViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.isValidEmail
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment : Fragment() {

    //private val model :LoginViewModel by viewModels()
    private val model:AuthViewModel by viewModels()


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.loginBtn.setOnClickListener {
            if (validation()){
                model.login(
                    email = binding.tfEmailLogin.text.toString(),
                    password = binding.tfPasswordLogin.text.toString()
                )
            }
        }

        model.login.observe(requireActivity()){state->
            when(state){
                is UiState.Loading -> {
                    binding.tfEmailLogin.text.clear()
                    //binding.loginProgress.show()
                }
                is UiState.Failure -> {
                    //binding.loginBtn.setText("Login")
                    //binding.loginProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    //binding.loginBtn.setText("Login")
                    //binding.loginProgress.hide()
                    toast(state.data)
                    //findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                }
            }
        }



        binding.registerLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_registerEmployessFragment)
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.tfEmailLogin.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.ingresa_Correo))
        }else{
            if (!binding.tfEmailLogin.text.toString().isValidEmail()){
                isValid = false
                toast(getString(R.string.correo_no_Valido))
            }
        }
        if (binding.tfPasswordLogin.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.ingresa_Contraseña))
        }else{
            if (binding.tfPasswordLogin.text.toString().length < 8){
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}