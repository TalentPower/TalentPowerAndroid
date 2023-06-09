package stg.talentpower.usa.app.talentpowerandroid.UI.Driver.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentAttendanceDriverBinding

@AndroidEntryPoint
class FragmentAttendanceDriver : Fragment() {

    private var _binding: FragmentAttendanceDriverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val attendanceViewModel =
            ViewModelProvider(this).get(AttendanceViewModel::class.java)

        _binding = FragmentAttendanceDriverBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        attendanceViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}