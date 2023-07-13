package stg.talentpower.usa.app.talentpowerandroid.UI.Driver.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentStartedRouteDriverBinding

class FragmentStartedRouteDriver : Fragment() {

    private var _binding:FragmentStartedRouteDriverBinding?=null
    private val binding get() = _binding!!

    private val model: StartedRouteDriverViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding= FragmentStartedRouteDriverBinding.inflate(inflater,container,false)
        return binding.root
    }





}