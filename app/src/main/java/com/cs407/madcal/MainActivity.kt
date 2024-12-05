package com.cs407.madcal

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    private var job : Job? = null
    private lateinit var downloadButton : Button
    private lateinit var selectionButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        downloadButton = findViewById(R.id.download_button)
        selectionButton = findViewById(R.id.selection_button)
    }

    private suspend fun fileDownloader() {

        withContext(Dispatchers.Main) {
            downloadButton.text = getString(R.string.downloading)
        }

        for (downloadProgress in 0..100 step 50) {
            Log.d(TAG, "Download Progress $downloadProgress%")
            withContext(Dispatchers.Main) {
                downloadButton.text = getString(R.string.download_progress, downloadProgress)
            }
            delay(1000) // Coroutine-friendly delay
        }

        withContext(Dispatchers.Main) {
            downloadButton.text = getString(R.string.download_button_text)
            Snackbar.make(
                downloadButton, // Using downloadButton as anchor view
                R.string.download_complete,
                Snackbar.LENGTH_LONG
            ).setAnchorView(selectionButton) // This will show it above the button
                .show()
        }
    }

    fun startDownload(view: View) {
        // Start the coroutine for downloading
        job = CoroutineScope(Dispatchers.Default).launch {
            fileDownloader()
        }
    }
}