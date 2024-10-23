package com.example.api_list

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.api_list.databinding.ActivityEditBinding
import com.example.api_list.databinding.ActivityItemDetailBinding
import com.example.api_list.model.Item
import com.example.api_list.model.ItemValue
import com.example.api_list.service.Result
import com.example.api_list.service.RetrofitClient
import com.example.api_list.service.safeApiCall
import com.example.api_list.ui.loadUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var item: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            patchItem()
        }

        loadItem()
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

    private fun patchItem() {
        CoroutineScope(Dispatchers.IO).launch {
            var result = safeApiCall {
                RetrofitClient.apiService.patchItem(item.id, ItemValue(
                    item.id,
                    binding.etName.text.toString(),
                    binding.etSurname.text.toString(),
                    binding.etProfession.text.toString(),
                    binding.etImageUrl.text.toString(),
                    binding.etAge.text.toString().toInt(),
                    location = null,
                    Date()
                ))
            }
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@EditActivity,
                            getString(R.string.erro_edit),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Success -> {
                        Toast.makeText(
                            this@EditActivity,
                            getString(R.string.item_editado_com_sucesso),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        binding.etId.setText(item.id)
        binding.etName.setText(item.value.name)
        binding.etSurname.setText(item.value.surname)
        binding.etProfession.setText(item.value.profession)
        binding.etImageUrl.setText(item.value.imageUrl)
        binding.etAge.setText(item.value.age.toString())
    }

    companion object {
        private const val ARG_ID = "ARG_ID"
        fun newIntent(itemDetailActivity: ItemDetailActivity, id: String): Intent? {
            val intent = Intent(itemDetailActivity, EditActivity::class.java)
            intent.putExtra(ARG_ID, id)
            return intent
        }
    }
}