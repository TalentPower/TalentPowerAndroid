package stg.talentpower.usa.app.talentpowerandroid.UI.Login.UI

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Login.ViewModel.ForgotPasswordViewModel

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }



}