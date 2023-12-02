package com.example.roomtest

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.roomtest.databinding.ActivityCostsBinding

class CostsActivity : AppCompatActivity() {
    lateinit var binding: ActivityCostsBinding
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = MainDb.getDb(this)
        val dao = db.getDao()

        dao.getAllItem().asLiveData().observe(this) { list ->
            binding.costsList.text = ""
            totalAmount = 0.0

            list.forEach {
                val priceText = if (it.price.isNotEmpty()) {
                    totalAmount += it.price.toDouble()
                    " - ${it.price}$"
                } else {
                    ""
                }
                val text = "${it.name}$priceText\n"
                binding.costsList.append(text)
            }

            updateTotalAmount()
        }

        binding.saveButton.setOnClickListener {
            val item = Item(
                null,
                binding.writeName.text.toString(),
                binding.writePrice.text.toString()
            )

            Thread {
                dao.insertItem(item)

                hideKeyboard()
            }.start()
        }

        binding.deleteButton.setOnClickListener {
            Thread {
                dao.deleteAllItems()
                totalAmount = 0.0
                runOnUiThread {
                    updateTotalAmount()
                }
            }.start()
        }
    }

    private fun updateTotalAmount() {
        binding.sumTotal.text = "$totalAmount$"
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
