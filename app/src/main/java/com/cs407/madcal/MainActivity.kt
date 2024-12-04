package com.cs407.madcal

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class MainActivity : AppCompatActivity() {
    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        val selectionButton: Button = findViewById(R.id.selection_button)
        val downloadButton: Button = findViewById(R.id.download_button)

        // Set click listener for "Download Calendar" button
        downloadButton.setOnClickListener {
            if (!isPermissionGranted) {
                if (isWritePermissionGranted()) {
                    // If already granted, proceed with downloading
                    isPermissionGranted = true
                    downloadCalendar()
                } else {
                    // Show custom permission dialog before requesting permission
                    showPermissionDialog()
                }
            } else {
                // Permission already granted, proceed
                downloadCalendar()
            }
        }
    }
    private fun isWritePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+, assume permission is granted
            isPermissionGranted
        } else {
            // For Android 9 (API 28) and below, check WRITE_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    // Show custom permission dialog
    private fun showPermissionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_permission, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Handle Yes/No button clicks
        dialogView.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            // Request system permission
            requestWritePermission()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_no).setOnClickListener {
            // Permission denied: Show access denied dialog
            showAccessDeniedDialog()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Request WRITE_EXTERNAL_STORAGE permission
    private fun requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Toast.makeText(this, "No permission required for Scoped Storage", Toast.LENGTH_SHORT).show()
            isPermissionGranted = true
            downloadCalendar()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION
            )
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            when {
                grantResults.isEmpty() -> {
                    // User dismissed the dialog without choosing
                    isPermissionGranted = false
                    showAccessDeniedDialog()
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted
                    isPermissionGranted = true
                    Toast.makeText(this, "Permission Granted. You can download the calendar now!", Toast.LENGTH_SHORT).show()
                    downloadCalendar()
                }
                else -> {
                    // Permission denied
                    isPermissionGranted = false
                    showAccessDeniedDialog()
                }
            }
        }
    }

    // Show access denied dialog
    private fun showAccessDeniedDialog() {
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

    // Simulate downloading the calendar
    private fun downloadCalendar() {
        Toast.makeText(this, "Downloading calendar...", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_WRITE_PERMISSION = 100
    }
}


