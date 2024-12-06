package com.cs407.madcal

import androidx.activity.enableEdgeToEdge
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val TAG = "MainActivity"
    private var isPermissionGranted = false
    private var job: Job? = null

    private lateinit var downloadButton: Button
    private lateinit var selectionButton: Button

    companion object {
        private const val REQUEST_WRITE_PERMISSION = 100
    }

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

        // Set click listener for "Download Calendar" button
        downloadButton.setOnClickListener {
            if (!isPermissionGranted) {
                if (isWritePermissionGranted()) {
                    isPermissionGranted = true
                    startDownload()
                } else {
                    showPermissionDialog()
                }
            } else {
                startDownload()
            }
        }
    }

    private fun isWritePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isPermissionGranted
        } else {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_permission, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            requestWritePermission()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_no).setOnClickListener {
            disableDownloadButton() // Disable the button if permission is denied
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isPermissionGranted = true
            startDownload()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
                startDownload()
            } else {
                disableDownloadButton() // Disable the button if permission is denied
            }
        }
    }
    private fun disableDownloadButton() {
        // Disable the button and set the color
        downloadButton.isEnabled = false
        downloadButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)

        // Show the denied message dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_access_denied, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
            delay(1000)
        }

        withContext(Dispatchers.Main) {
            downloadButton.text = getString(R.string.download_button_text)
            Snackbar.make(downloadButton, R.string.download_complete, Snackbar.LENGTH_LONG)
                .setAnchorView(selectionButton)
                .show()
        }
    }

    private fun startDownload() {
        job = CoroutineScope(Dispatchers.Default).launch {
            fileDownloader()
        }
    }
}

