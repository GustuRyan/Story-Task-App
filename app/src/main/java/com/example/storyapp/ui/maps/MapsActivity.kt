package com.example.storyapp.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.data.repository.StoryRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.ui.MainActivity
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel
        val repository = StoryRepository()
        mapsViewModel = ViewModelProvider(this, MapsViewModelFactory(repository)).get(MapsViewModel::class.java)

        // Observe data
        observeStories()

        // Setup Map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        getMyLocation()

        // Show ProgressBar while loading data
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            mapsViewModel.fetchStoriesWithLocation()
            Log.d("MapsActivity", "API request initiated for stories with location.")
        }
    }

    private fun observeStories() {
        // Observe LiveData untuk menampilkan marker
        mapsViewModel.storiesWithLocation.observe(this) { stories ->
            // Hide ProgressBar once data is received
            binding.progressBar.visibility = View.GONE

            if (stories != null) {
                Log.d("MapsActivity", "Stories received: ${stories.size} stories found.")
                stories.forEach { story ->
                    val latLng = LatLng(story.lat, story.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                    )
                }

                if (stories.isNotEmpty()) {
                    val firstLocation = LatLng(stories[0].lat, stories[0].lon)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f))
                }
            } else {
                Log.d("MapsActivity", "No stories received or data is empty.")
            }
        }

        // Handle error
        mapsViewModel.errorMessage.observe(this) { error ->
            // Hide ProgressBar on error
            binding.progressBar.visibility = View.GONE
            Log.e("MapsActivity", "Error occurred: $error")
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            Log.d("MapsActivity", "Location permission granted. Showing user's location on map.")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            Log.d("MapsActivity", "Location permission denied or not granted yet.")
        }
    }

    // Override onBackPressed to navigate to MainActivity
    @Deprecated("")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Optional: Close the current activity
    }
}