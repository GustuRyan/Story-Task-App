package com.example.storyapp.ui.add_story

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.compressImage
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.databinding.FragmentAddStoryBinding
import com.example.storyapp.getImageUri
import com.example.storyapp.ui.home.HomeFragment
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private lateinit var viewModel: AddStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storyRepository = StoryRepository()
        val factory = AddStoryViewModelFactory(storyRepository)
        viewModel = ViewModelProvider(this, factory).get(AddStoryViewModel::class.java)

        binding.progressBar.visibility = View.GONE

        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { requestCameraPermission() }

        binding.buttonAdd.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            uploadStory()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showPermissionExplanationDialog()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Log.e("Permission", "Camera permission denied")
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Izin Kamera Diperlukan")
            .setMessage("Aplikasi ini membutuhkan izin kamera untuk mengambil foto.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let {
                showImage()
            }
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            val compressedFile = compressImage(requireContext(), uri)
            if (compressedFile != null) {
                val compressedUri = Uri.fromFile(compressedFile)
                Log.d("Compressed Image", "Path: ${compressedFile.path}, Size: ${compressedFile.length()} bytes")
                binding.ivStoryImage.setImageURI(compressedUri)

                currentImageUri = compressedUri
            } else {
                Log.e("Compression", "Failed to compress image.")
            }
        }
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString().trim()

        if (description.isEmpty() || currentImageUri == null) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Deskripsi dan gambar harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val photo = getMultipartFromUri(currentImageUri!!)

        // Cek apakah switch diaktifkan
        val isLocationEnabled = binding.swAddLocation.isChecked

        var latRequestBody: RequestBody? = null
        var lonRequestBody: RequestBody? = null

        // Jika switch diaktifkan, ambil lokasi
        if (isLocationEnabled) {
            getCurrentLocation { lat, lon ->
                latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                // Lanjutkan dengan upload story
                viewModel.uploadStory(photo, descriptionRequestBody, latRequestBody, lonRequestBody) { success ->
                    handleUploadResult(success)
                }
            }
        } else {
            // Jika switch tidak diaktifkan, lanjutkan upload tanpa lokasi
            viewModel.uploadStory(photo, descriptionRequestBody) { success ->
                handleUploadResult(success)
            }
        }
    }

    private fun getCurrentLocation(callback: (Double?, Double?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val lat = location?.latitude
                val lon = location?.longitude
                callback(lat, lon)
            }
        } else {
            // Minta permission jika belum diberikan
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            callback(null, null)
        }
    }

    private fun handleUploadResult(success: Boolean) {
        binding.progressBar.visibility = View.GONE
        if (success) {
            Toast.makeText(requireContext(), "Story berhasil diupload", Toast.LENGTH_SHORT).show()

            // Navigasi ke HomeFragment dengan argumen shouldRefresh = true
            val action = AddStoryFragmentDirections.actionAddStoryFragmentToHomeFragment(shouldRefresh = true)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Gagal mengupload story", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMultipartFromUri(uri: Uri): MultipartBody.Part {
        val file = File(uri.path)
        val requestBody = file.asRequestBody("image/*".toMediaType())
        return MultipartBody.Part.createFormData("photo", file.name, requestBody)
    }

}