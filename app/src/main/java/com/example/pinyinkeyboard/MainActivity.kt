package com.example.pinyinkeyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : Activity() {

    private lateinit var suggestionsAdapter: SuggestionsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val suggestionsRecyclerView = findViewById<RecyclerView>(R.id.suggestionsRecyclerView)
        suggestionsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        suggestionsAdapter = SuggestionsAdapter(emptyList()) { suggestion ->
            // Handle suggestion click
            onSuggestionClick(suggestion)
        }

        suggestionsRecyclerView.adapter = suggestionsAdapter

        // Example: Update suggestions
        updateSuggestions(listOf("你好", "谢谢", "再见"))


//        Hiding On-screen Keyboard
//        val editText = findViewById<EditText>(R.id.editTextText)
//        editText.setOnTouchListener { _, _ ->
//            // Request focus to ensure the EditText is focused
//            editText.requestFocus()
//            // Manually hide the keyboard
//            hideKeyboard(editText)
//            true
//        }
    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateSuggestions(suggestions: List<String>) {
        suggestionsAdapter.updateSuggestions(suggestions)
    }

    private fun onSuggestionClick(suggestion: String) {
        // Implement what happens when a suggestion is clicked
        // For example, append the suggestion to the EditText
    }
}