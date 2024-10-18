package com.example.api_list

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.api_list.adapter.ItemAdapter
import com.example.api_list.databinding.ActivityMainBinding
import com.example.api_list.model.Item
import com.example.api_list.service.Result
import com.example.api_list.service.RetrofitClient
import com.example.api_list.service.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun onResume() {
        super.onResume()

        fetchItems()
    }

    private fun fetchItems() {
        // Alterando execução para thread IO
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall {
                RetrofitClient.apiService.getItems()
            }
            // Alterando execução para Main thread
            withContext(Dispatchers.Main) {
                binding.swipeRefreshLayout.isRefreshing = false
                when (result) {
                    is Result.Error -> {}
                    is Result.Success -> handleOnSuccess(result.data)
                }
            }
        }
    }

    private fun handleOnSuccess(data: List<Item>) {
        val adapter = ItemAdapter(data) {
            startActivity(ItemDetailActivity.newIntent(this, it.id))
        }

        binding.recyclerView.adapter = adapter
    }

    private fun setupView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            fetchItems()
        }
        binding.addCta.setOnClickListener{
            startActivity(NewItemActivity.newIntent(this))
        }
    }
}