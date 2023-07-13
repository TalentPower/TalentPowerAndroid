package stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import stg.talentpower.usa.app.talentpowerandroid.Model.Driver
import stg.talentpower.usa.app.talentpowerandroid.Model.ImagesModel
import stg.talentpower.usa.app.talentpowerandroid.R
import stg.talentpower.usa.app.talentpowerandroid.UI.Employess.home.ViewModels.AddDriverViewModel
import stg.talentpower.usa.app.talentpowerandroid.Util.UiState
import stg.talentpower.usa.app.talentpowerandroid.Util.UploadImagesDriverConstants
import stg.talentpower.usa.app.talentpowerandroid.Util.createDialog
import stg.talentpower.usa.app.talentpowerandroid.Util.hide
import stg.talentpower.usa.app.talentpowerandroid.Util.show
import stg.talentpower.usa.app.talentpowerandroid.Util.toast
import stg.talentpower.usa.app.talentpowerandroid.databinding.FragmentAddDriverBinding

@AndroidEntryPoint
class AddDriverFragment : Fragment() {

    private var _binding: FragmentAddDriverBinding? = null
    private val binding get() = _binding!!

    private val model: AddDriverViewModel by viewModels()

    private var listImages : MutableList<ImagesModel> = arrayListOf()
    private var case=""
    private lateinit var dialog: Dialog
    private lateinit var driver:Driver


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentAddDriverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = requireContext().createDialog(R.layout.loading_layout, false)

        binding.btnSaveDriver.setOnClickListener {

             driver= Driver(
                "",
                 "",
                binding.tfName.text.toString(),
                binding.tfAddress.text.toString(),
                binding.tfPhone.text.toString(),
                binding.tfPhoneEmergency.text.toString(),
                binding.tfNSS.text.toString(),
                binding.tfBankKey.text.toString(),
                 "",
                "driver",
                "",
                listImages
            )

            driver.apply {
                model.registerDriver(binding.tfEmail.text.toString(),binding.tfPassword.text.toString(), driver)
            }
        }
        binding.btnAddPhoto.setOnClickListener {
            checkPermisos(0)

        }
        binding.btnActa.setOnClickListener {
            case=UploadImagesDriverConstants.ACTA_NACIMIENTO
            selectFile()
        }
        binding.btnCURP.setOnClickListener {
            case=UploadImagesDriverConstants.CURP
            selectFile()
        }
        binding.btnINE.setOnClickListener {
            case=UploadImagesDriverConstants.INE
            selectFile()
        }
        binding.btnNSS.setOnClickListener {
            case=UploadImagesDriverConstants.NSS
            selectFile()
        }
        binding.btnCertEstudios.setOnClickListener {
            case=UploadImagesDriverConstants.CERT_ESTUDIOS
            selectFile()
        }
        binding.btnSolicitud.setOnClickListener {
            case=UploadImagesDriverConstants.SOLI_EMPLEO
            selectFile()
        }
        binding.btnRFC.setOnClickListener {
            case=UploadImagesDriverConstants.RFC
            selectFile()
        }
        binding.btnCertEstudios.setOnClickListener {
            case=UploadImagesDriverConstants.CERT_ESTUDIOS
            selectFile()
        }
        binding.btnLicencia.setOnClickListener {
            case=UploadImagesDriverConstants.LICENCIA
            selectFile()
        }

        model.register.observe(requireActivity()){ state->
            if (state!=null){
                when(state){
                    is UiState.Success->{
                        dialog.dismiss()
                        clear()
                        toast(state.data)
                    }
                    is UiState.Failure->{
                        state.error?.let { error->
                            val editText = dialog.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtDialog)
                            editText.text = error
                        }
                    }
                    is UiState.Loading->{
                        dialog.show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermisos(type: Int) {
        when (type) {
            0 -> if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                takeImage()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showAlertPermision()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showAlertPermision() {
        TODO("Not yet implemented")
    }

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            takeImage()
        } else {
            showAlertPermision()
        }
    }

    private fun takeImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.launch(cameraIntent)
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageBitmap: Bitmap = it.data?.extras?.get("data") as Bitmap
            binding.btnAddPhoto.setImageBitmap(imageBitmap)
        }
    }

    private fun selectFile(){
        activityResultLauncher.launch("image/*")
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val img= ImagesModel()
        img.image=it
        when(case){
            UploadImagesDriverConstants.ACTA_NACIMIENTO->{
                img.name= UploadImagesDriverConstants.ACTA_NACIMIENTO
                binding.btnActa.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.LICENCIA->{
                img.name=UploadImagesDriverConstants.LICENCIA
                listImages.add(img)
                binding.btnLicencia.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.RFC->{
                img.name=UploadImagesDriverConstants.RFC
                listImages.add(img)
                binding.btnRFC.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.NSS->{
                img.name=UploadImagesDriverConstants.NSS
                listImages.add(img)
                binding.btnNSS.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.SOLI_EMPLEO->{
                img.name=UploadImagesDriverConstants.SOLI_EMPLEO
                listImages.add(img)
                binding.btnSolicitud.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.CERT_ESTUDIOS->{
                img.name=UploadImagesDriverConstants.CERT_ESTUDIOS
                listImages.add(img)
                binding.btnCertEstudios.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.CURP->{
                img.name=UploadImagesDriverConstants.CURP
                listImages.add(img)
                binding.btnCURP.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
            UploadImagesDriverConstants.INE->{
                img.name=UploadImagesDriverConstants.INE
                listImages.add(img)
                binding.btnINE.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_done)
            }
        }
    }

    private fun clear(){
        binding.tfAddress.text.clear()
        binding.tfName.text.clear()
        binding.tfPhone.text.clear()
        binding.tfPhoneEmergency.text.clear()
        binding.tfBankKey.text.clear()
        binding.tfNSS.text.clear()
        binding.tfEmail.text.clear()
        binding.tfPassword.text.clear()
        binding.btnINE.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnCURP.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnSolicitud.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnCertEstudios.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnRFC.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnLicencia.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnActa.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        binding.btnNSS.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
    }

    override fun onStart() {
        super.onStart()
        val nav=requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.hide()
        val toolbar=requireActivity().findViewById<Toolbar>(R.id.toolBarActivity)
        toolbar.show()
    }
}