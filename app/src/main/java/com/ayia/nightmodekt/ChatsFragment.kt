// ChatsFragment.kt
package com.ayia.nightmodekt

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

class ChatsFragment : Fragment() {

    companion object {
        const val EXTRA_SONG_TITLE = "extra_song_title"
    }

    private lateinit var searchButton: ImageButton
    private lateinit var titleTextView: TextView
    private lateinit var songsListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var songs: List<SongLyric>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chats, container, false)

        searchButton = rootView.findViewById(R.id.searchButton)
        titleTextView = rootView.findViewById(R.id.titleTextView)
        songsListView = rootView.findViewById(R.id.songsListView)

        songs = loadSongLyricsFromJson(requireContext())
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            songs.map { "${it.id}    ${it.title}" }
        )
        songsListView.adapter = adapter

        searchButton.setOnClickListener {
            showSearchDialog()
        }

        songsListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedSong = songs[position]
                showLyricsActivity(selectedSong.id, selectedSong.title, selectedSong.lyrics)
            }

        return rootView
    }

    private fun loadSongLyricsFromJson(context: Context): List<SongLyric> {
        val gson = Gson()
        val songListType: Type = object : TypeToken<List<SongLyric>>() {}.type
        var songs: List<SongLyric> = emptyList()

        try {
            val assetManager = context.assets
            val inputStream: InputStream = assetManager.open("song_lyrics.json")
            val reader = InputStreamReader(inputStream)

            songs = gson.fromJson(reader, songListType)

            reader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return songs
    }

    private fun showSearchDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_search, null)
        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val resultsListView = dialogView.findViewById<ListView>(R.id.resultsListView)
        val noResultsTextView = dialogView.findViewById<TextView>(R.id.noResultsTextView)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        // Automatically focus on the search input and show the keyboard
        searchEditText.requestFocus()
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

        searchEditText.addTextChangedListener {
            val query = it.toString().toLowerCase()
            val filteredSongs =
                songs.filter { song -> song.title.toLowerCase().contains(query) || song.id.toString() == query }
            if (filteredSongs.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    filteredSongs.map { "${it.id}    ${it.title}" }
                )
                resultsListView.adapter = adapter
                resultsListView.visibility = View.VISIBLE
                noResultsTextView.visibility = View.GONE

                resultsListView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val selectedSong = filteredSongs[position]
                        showLyricsActivity(selectedSong.id, selectedSong.title, selectedSong.lyrics)
                        alertDialog.dismiss()
                    }
            } else {
                resultsListView.visibility = View.GONE
                noResultsTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun showLyricsActivity(songId: Int, title: String, lyrics: String) {
        val intent = Intent(requireContext(), LyricsActivity::class.java).apply {
            putExtra(LyricsActivity.EXTRA_SONG_ID, songId)
            putExtra(EXTRA_SONG_TITLE, title) // Use the constant here
            putExtra(LyricsActivity.EXTRA_SONG_LYRICS, lyrics)
        }
        startActivity(intent)
    }

    data class SongLyric(val id: Int, val title: String, val lyrics: String)
}
