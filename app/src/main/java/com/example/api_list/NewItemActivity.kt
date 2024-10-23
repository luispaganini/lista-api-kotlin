package com.example.api_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.api_list.databinding.ActivityNewItemBinding
import com.example.api_list.model.Item
import com.example.api_list.model.ItemLocation
import com.example.api_list.model.ItemValue
import com.example.api_list.service.Result
import com.example.api_list.service.RetrofitClient
import com.example.api_list.service.safeApiCall
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.Date

class NewItemActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityNewItemBinding
    private lateinit var mMap: GoogleMap
    private lateinit var item: Item
    private lateinit var itemLocation: ItemLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupGoogleMap()
    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions().position(latLng).title("Local Selecionado")
                )

                itemLocation = ItemLocation(
                    name = binding.name.text.toString(),
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )

                Toast.makeText(
                    this, "Coordenada: ($itemLocation.latitude, $itemLocation.longitude)", Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.googleMapContent.visibility = View.VISIBLE
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.saveCta.setOnClickListener {
            saveItem()
        }
    }

    private fun saveItem() {
        if (!validateForm())
            return

        CoroutineScope(Dispatchers.IO).launch {
            val id = SecureRandom().nextInt().toString()
            val itemValue = ItemValue(
                id = id,
                name = binding.name.text.toString(),
                surname = binding.surname.text.toString(),
                profession = binding.profession.text.toString(),
                imageUrl = binding.imageUrl.text.toString(),
                age = binding.age.text.toString().toInt(),
                location = itemLocation,
                date = Date()
            )

            val result = safeApiCall {
                RetrofitClient.apiService.createItem(itemValue)
            }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@NewItemActivity,
                            getString(R.string.error_create),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@NewItemActivity,
                            getString(R.string.success_create, result.data.id),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.name.text.toString().isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_validate_form, "Name"),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.surname.text.toString().isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_validate_form, "Surname"),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.age.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Age"), Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (binding.imageUrl.text.toString().isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_validate_form, "Image Url"),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.profession.text.toString().isBlank()) {
            Toast.makeText(
                this,
                getString(R.string.error_validate_form, "Address"),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, NewItemActivity::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (::item.isInitialized) {
            loadItemLocationInGoogleMap()
        }
    }

    private fun loadItemLocationInGoogleMap() {
        item.value.location?.let {
            binding.googleMapContent.visibility = View.VISIBLE
            val latLng = LatLng(it.latitude, it.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
            )
            mMap.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(latLng, 16f)
            )
        }
    }
}