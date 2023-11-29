package com.ayia.nightmodekt

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.core.widget.addTextChangedListener

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var songsListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var songs: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.searchEditText)
        songsListView = findViewById(R.id.songsListView)

        songs = intent.getStringArrayListExtra("songs") ?: emptyList()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songs)
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { parent, view, position, id ->
            val selectedSong = parent.getItemAtPosition(position) as String
            val resultIntent = Intent()
            resultIntent.putExtra("selectedSong", selectedSong)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        searchEditText.addTextChangedListener {
            adapter.filter.filter(it)
        }
    }
}
