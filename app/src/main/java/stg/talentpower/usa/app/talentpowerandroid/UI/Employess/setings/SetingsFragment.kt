package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.setings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Login.UI.AuthActivity
import stg.talentpower.usa.app.talentpowerandroid.Util.disable
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentSetingsEmployessBinding

@AndroidEntryPoint
class SetingsFragment : Fragment() {

    private var _binding: FragmentSetingsEmployessBinding? = null

    private val binding get() = _binding!!

    private val model:SetingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("lifecyc","onCreateView")
        _binding = FragmentSetingsEmployessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecyc","onResume")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lifecyc","onViewCreated")


        binding.btnLogOut.setOnClickListener {
            model.logOut()
            val i= Intent(requireActivity(),AuthActivity::class.java)
            activity?.startActivity(i)
            activity?.finish()
        }

        binding.cardAddEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_employee_to_registerEmployessFragment2)
        }
        binding.cardAddRoute.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_employee_to_addRouteFragment)
        }

        binding.cardAddClient.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_employee_to_addClientFragment)
        }

        binding.cardAddDriver.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_employee_to_addDriverFragment)
        }



        if (model.employee.value==null)model.getDataUser()

        observers()
    }

    private fun observers(){
        model.employee.observe(requireActivity()){ data->
            if (data!=null){
                binding.txtNameProfileEmployes.text=data.name
                binding.txtPhoneProfileEmployes.text=data.phone
                binding.txtAreaProfileEmployes.text=data.area
                Glide.with(requireActivity()) //1
                    .load("https://i.pinimg.com/474x/4d/69/98/4d6998832238bf2ed5a7195612401f11.jpg")
                    .placeholder(R.drawable.ic_acount_24)
                    .error(R.drawable.ic_image_error_24)
                    .skipMemoryCache(true) //2
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                    .transform(CircleCrop()) //4
                    .into(binding.imgProfileEmployes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("lifecyc","onDestroyView")
    }
}