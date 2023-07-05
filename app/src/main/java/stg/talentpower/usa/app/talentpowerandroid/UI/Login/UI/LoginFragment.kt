package stg.talentpower.usa.app.talentpowerandroid.UI.Login.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import stg.talentpower.usa.app.talentpowerandroid.Model.Client
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver2
import stg.talentpower.usa.app.talentpowerandroid.Model.Employee
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Driver.DriverActivity
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.EmployessActivity
import stg.talentpower.usa.app.talentpowerandroid.UI.Login.ViewModel.AuthViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.isValidEmail
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val model:AuthViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginBtn.setOnClickListener{
            if (validation()){
                model.login(
                    email = binding.tfEmailLogin.text.toString(),
                    password = binding.tfPasswordLogin.text.toString(),
                )
            }
        }

        model.login.observe(requireActivity()){state->
            when(state){
                is UiState.Loading -> {
                    binding.loginProgress.show()
                    binding.loginBtn.text = ""
                }
                is UiState.Failure -> {
                    toast(state.error)
                    clear()
                }
                is UiState.Success -> {
                    when(state.data){
                        is Employee->{
                            clear()
                            val i= Intent(requireContext(), EmployessActivity::class.java)
                            activity?.startActivity(i)
                            activity?.finish()
                        }
                        is Driver2 ->{
                            clear()
                            val i= Intent(requireContext(), DriverActivity::class.java)
                            activity?.startActivity(i)
                            activity?.finish()
                        }
                        is Client->{
                            clear()
                            toast("Client not available now")
                        }
                    }
                }
                else -> {}
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

    override fun onStart() {
        super.onStart()
        model.getSession {user->
            if (user!=null){
                when(user){
                    is Driver2->{
                        Log.d("rol","Soy driver")
                    }
                    is Employee->{
                        requireActivity().finish()
                        val i=Intent(requireContext(),EmployessActivity::class.java)
                        activity?.startActivity(i)
                        activity?.finish()
                    }
                    is Client->{
                        Log.d("rol","Soy client")
                    }
                }
            }
        }
    }
    private fun clear(){
        binding.tfEmailLogin.text.clear()
        binding.tfPasswordLogin.text.clear()
        binding.loginProgress.hide()
        binding.loginBtn.text = "Login"
    }
}