package com.example.api_list


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.api_list.adapter.ItemAdapter
import com.example.api_list.databinding.ActivityItemDetailBinding
import com.example.api_list.model.Item
import com.example.api_list.service.Result
import com.example.api_list.service.RetrofitClient
import com.example.api_list.service.safeApiCall
import com.example.api_list.ui.CircleTransform
import com.example.api_list.ui.loadUrl
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var item: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        loadItem()
    }

    override fun onResume() {
        loadItem()
        super.onResume()
    }

    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall {
                RetrofitClient.apiService.getItem(itemId)
            }
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        item = result.data
                        handleSuccess()
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        binding.name.text = "${item.value.name} ${item.value.surname}"
        binding.age.text = getString(R.string.item_age, item.value.age.toString())
        binding.address.text = item.value.address
        binding.image.loadUrl(item.value.imageUrl)

    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.deleteCTA.setOnClickListener {
            deleteItem()
        }

        binding.editActionButton.setOnClickListener {
            startActivity(EditActivity.newIntent(this, item.id))
        }
    }

    private fun deleteItem() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall {
                RetrofitClient.apiService.deleteItem(item.id)
            }
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@ItemDetailActivity,
                            getString(R.string.error_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Success -> {
                        Toast.makeText(
                            this@ItemDetailActivity,
                            getString(R.string.success_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_ID = "ARG_ID"

        fun newIntent(
            context: Context, itemId: String
        ) = Intent(context, ItemDetailActivity::class.java).apply {
            putExtra(ARG_ID, itemId)
        }
    }
}