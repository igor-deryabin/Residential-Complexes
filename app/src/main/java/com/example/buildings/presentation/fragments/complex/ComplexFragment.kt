package com.example.buildings.presentation.fragments.complex

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.buildings.R
import com.example.buildings.databinding.FragmentComplexBinding
import com.example.buildings.domain.data.Complex
import com.example.buildings.domain.data.ViewMode
import com.example.buildings.utils.EXTERNAL_STORAGE_PERMISSIONS_REQUIRED
import com.example.buildings.utils.Result
import com.example.buildings.utils.extension.hasPermissions
import com.example.buildings.utils.extension.hideKeyboard
import com.example.buildings.utils.extension.longToast
import com.example.buildings.utils.getFileFromUri
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComplexFragment: Fragment() {

    private var _binding: FragmentComplexBinding? = null
    private val binding get() = _binding!!

    private val vm: ComplexViewModel by viewModel()

    private var viewMode = ViewMode.ADD
    private var complex: Complex? = null

    private var imagePath: String? = null
    private lateinit var permissionRequestGallery: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermissionRequestGallery()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentComplexBinding.inflate(inflater, container, false)
        val root = binding.root

        KeyboardVisibilityEvent.setEventListener(requireActivity(), viewLifecycleOwner) {
            if (!it) {
                binding.apply {
                    etName.clearFocus()
                    etArea.clearFocus()
                    etCity.clearFocus()
                    etMetro.clearFocus()
                    etYear.clearFocus()
                    etFloors.clearFocus()
                    etCost.clearFocus()
                    etBuildingsCount.clearFocus()
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                longToast(it)
            }
        }
        vm.complexLoaded.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let {
                        complex = it
                        initUI()
                    }
                }
                Result.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    result.error?.let {
                        it.message?.let { it1 -> longToast(it1) }
                    }
                }
                Result.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

        }
        vm.complexInserted.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let {
                        viewMode = ViewMode.VIEW
                        vm.getComplexFromId(it)
                    }
                }
                Result.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    result.error?.let {
                        it.message?.let { it1 -> longToast(it1) }
                    }
                }
                Result.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
        vm.complexRemoved.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let {
                        findNavController().navigateUp()
                    }
                }
                Result.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    result.error?.let {
                        it.message?.let { it1 -> longToast(it1) }
                    }
                }
                Result.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        arguments?.let { b ->
            val complexId = b.getInt("complex_id", -1)
            if (complexId != -1) {
                viewMode = ViewMode.VIEW
                vm.getComplexFromId(complexId)
            }
        }

        if (viewMode == ViewMode.ADD) {
            binding.progressBar.visibility = View.GONE
            initUI()
        }

        binding.apply {
            etName.doAfterTextChanged { etName.error = null }
            etArea.doAfterTextChanged { etArea.error = null }
            etYear.doAfterTextChanged { etYear.error = null }
            etFloors.doAfterTextChanged { etFloors.error = null }
            etCost.doAfterTextChanged { etCost.error = null }
            etBuildingsCount.doAfterTextChanged { etBuildingsCount.error = null }

            rlBtnBack.setOnClickListener { findNavController().navigateUp() }

            rlBtnDone.setOnClickListener { attemptSave() }
            rlBtnEdit.setOnClickListener {
                viewMode = ViewMode.EDIT
                initUI()
            }
            rlBtnRemove.setOnClickListener { complex?.let { c -> vm.removeComplex(c) } }

            frameImage.setOnClickListener {
                if (viewMode != ViewMode.VIEW) {
                    if (requireActivity().hasPermissions(EXTERNAL_STORAGE_PERMISSIONS_REQUIRED)) {
                        dispatchGalleryIntent()
                    } else {
                        permissionRequestGallery.launch(EXTERNAL_STORAGE_PERMISSIONS_REQUIRED)
                    }
                }
            }
        }
    }

    private fun attemptSave() {
        val errorString = getString(R.string.error_empty)
        var isError = false
        var etError: AppCompatEditText? = null

        binding.apply {
            if (etName.text.isNullOrEmpty()) {
                isError = true
                etError = etName
            } else if (etArea.text.isNullOrEmpty()) {
                isError = true
                etError = etArea
            } else if (etYear.text.isNullOrEmpty() || etYear.text.toString() == "0") {
                isError = true
                etError = etYear
            } else if (etFloors.text.isNullOrEmpty() || etFloors.text.toString() == "0") {
                isError = true
                etError = etFloors
            } else if (etCost.text.isNullOrEmpty() || etCost.text.toString() == "0.0") {
                isError = true
                etError = etCost
            } else if (etBuildingsCount.text.isNullOrEmpty() || etBuildingsCount.text.toString() == "0") {
                isError = true
                etError = etBuildingsCount
            }

            if (isError) {
                etError?.let {
                    it.error = errorString
                    it.requestFocus()
                }
            } else {
                requireActivity().hideKeyboard()
                vm.insertComplex(
                    Complex(
                        name = etName.text.toString(),
                        image = imagePath ?: "" ,
                        area = etArea.text.toString(),
                        city = etCity.text.toString(),
                        metro = etMetro.text.toString(),
                        year = etYear.text.toString().toInt(),
                        floors = etFloors.text.toString().toInt(),
                        cost = etCost.text.toString().toDouble(),
                        buildingsCount = etBuildingsCount.text.toString().toInt(),
                        parking = switchParking.isChecked,
                        rented = switchRented.isChecked
                    )
                )
            }
        }
    }

    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultLauncherOpenGallery.launch(pickPhoto)
    }

    private fun initUI() {
        binding.apply {
            when (viewMode) {
                ViewMode.ADD -> {
                    ivLogo.visibility = View.GONE
                    tvTitle.text = getString(R.string.complex_title_add)
                    tvTitle.visibility = View.VISIBLE
                    rlBtnDone.visibility = View.VISIBLE
                    rlBtnEdit.visibility = View.GONE
                    rlBtnRemove.visibility = View.GONE
                    ivEditImage.visibility = View.VISIBLE

                    llParking.visibility = View.GONE
                    switchParking.visibility = View.VISIBLE
                    llRented.visibility = View.GONE
                    switchRented.visibility = View.VISIBLE

                    activateEdit(true)
                }
                ViewMode.VIEW -> {
                    ivLogo.visibility = View.VISIBLE
                    tvTitle.visibility = View.GONE
                    rlBtnDone.visibility = View.GONE
                    rlBtnEdit.visibility = View.VISIBLE
                    rlBtnRemove.visibility = View.VISIBLE
                    ivEditImage.visibility = View.GONE

                    llParking.visibility = View.VISIBLE
                    switchParking.visibility = View.GONE
                    llRented.visibility = View.VISIBLE
                    switchRented.visibility = View.GONE

                    updateDataUI()
                    activateEdit(false)
                }
                ViewMode.EDIT -> {
                    ivLogo.visibility = View.GONE
                    tvTitle.text = getString(R.string.complex_title_edit)
                    tvTitle.visibility = View.VISIBLE
                    rlBtnDone.visibility = View.VISIBLE
                    rlBtnEdit.visibility = View.GONE
                    rlBtnRemove.visibility = View.GONE
                    ivEditImage.visibility = View.VISIBLE

                    llParking.visibility = View.GONE
                    switchParking.visibility = View.VISIBLE
                    llRented.visibility = View.GONE
                    switchRented.visibility = View.VISIBLE

                    updateDataUI()
                    activateEdit(true)
                }
            }
        }
    }

    private fun updateDataUI() {
        complex?.let {
            binding.apply {
                if (it.image.isNotEmpty()) {
                    ivImage.setImageURI(it.image.toUri())
                }
                etName.setText(it.name)
                etArea.setText(it.area)
                etYear.setText(it.year.toString())
                etFloors.setText(it.floors.toString())
                etCost.setText(it.cost.toString())
                etBuildingsCount.setText(it.buildingsCount.toString())

                switchParking.isChecked = it.parking
                switchRented.isChecked = it.rented

                if (it.parking) {
                    tvParking.text = getString(R.string.complex_parking_yes)
                } else {
                    tvParking.text = getString(R.string.complex_parking_no)
                }

                if (it.rented) {
                    tvRented.text = getString(R.string.complex_rented_yes)
                } else {
                    tvRented.text = getString(R.string.complex_rented_no)
                }

                if (it.city.isEmpty()) {
                    llCity.visibility = View.GONE
                } else {
                    etCity.setText(it.city)
                }
                if (it.metro.isEmpty()) {
                    llMetro.visibility = View.GONE
                } else {
                    etMetro.setText(it.metro)
                }
            }
        }
    }

    private var resultLauncherOpenGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val uri = result.data!!.data
                    val tempFile = getFileFromUri(requireContext(), uri!!)
                    imagePath = tempFile.path
                    binding.ivImage.setImageURI(uri)
                }
                Activity.RESULT_CANCELED -> {}
            }
        }

    private fun registerPermissionRequestGallery() {
        permissionRequestGallery = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val granted = it.value
                val permission = it.key
                if (!granted) {
                    val neverAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                    if (!neverAskAgain) {
                        permissionRequestGallery.launch(arrayOf(permission))
                    }
                    return@registerForActivityResult
                }
            }
            dispatchGalleryIntent()
        }
    }

    private fun activateEdit(isActive: Boolean) {
        binding.apply {
            if (isActive) {
                llCity.visibility = View.VISIBLE
                llMetro.visibility = View.VISIBLE
            }

            etName.isFocusableInTouchMode = isActive
            etArea.isFocusableInTouchMode = isActive
            etCity.isFocusableInTouchMode = isActive
            etMetro.isFocusableInTouchMode = isActive
            etYear.isFocusableInTouchMode = isActive
            etFloors.isFocusableInTouchMode = isActive
            etCost.isFocusableInTouchMode = isActive
            etBuildingsCount.isFocusableInTouchMode = isActive
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}