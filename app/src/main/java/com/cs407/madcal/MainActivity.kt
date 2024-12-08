package com.cs407.madcal

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var job: Job? = null
    private lateinit var downloadButton: Button
    private lateinit var selectionButton: Button
    private val scope = CoroutineScope(Dispatchers.IO)

    // SharedPreferences keys
    private val PREFS_NAME = "AppPrefs"
    private val PREF_PERMISSION_DENIED = "permissionDenied"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseApp.initializeApp(this)

        downloadButton = findViewById(R.id.download_button)
        selectionButton = findViewById(R.id.selection_button)

        // Update the button state based on saved permission state
        updateButtonState()
        downloadButton.setOnClickListener {
            if (isPermissionDenied()) {
                // Show the "Access Denied" dialog if permission was denied
                showAccessDeniedDialog()
            } else {
                // Otherwise, proceed with permission check and download logic
                checkPermissionAndDownload()
            }
        }

        // Set click listener for the "Download Calendar" button
//        downloadButton.setOnClickListener {
//            checkPermissionAndDownload()
//        }

    }

    private fun checkPermissionAndDownload() {
        // Check if permission has been explicitly denied
        if (isPermissionDenied()) {
            // If permission was previously denied, disable the button and show a message
            disableDownloadButton()
            Toast.makeText(this, "Permission denied. Cannot download events.", Toast.LENGTH_SHORT).show()
        } else {
            // Check if the calendar permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                requestPermission()
            } else {
                // Permission is already granted; proceed to show the download confirmation dialog
                showDownloadConfirmationDialog()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted; clear any saved denial state and show the confirmation dialog
                savePermissionDeniedState(false)
                showDownloadConfirmationDialog()
            } else {
                // Permission denied; save the denial state
                savePermissionDeniedState(true)
                disableDownloadButton()
                showAccessDeniedDialog() // Show Access Denied Dialog

//                Toast.makeText(this, "Permission denied. Cannot download events.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDownloadConfirmationDialog() {
        // Show dialog prompting user about download
        AlertDialog.Builder(this)
            .setTitle("Download Events")
            .setMessage("If you want to download selected events, go to 'Selection'. Otherwise, we will download ALL recent events. Do you want to proceed?")
            .setPositiveButton("YES") { dialog, _ ->
                dialog.dismiss()
                // Start downloading events
                startDownloadAllEvents()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
                // Do nothing
            }
            .show()
    }

    private fun startDownloadAllEvents() {
        job = scope.launch {
            val events = fetchAllEventsFromFirestore()
            withContext(Dispatchers.Main) {
                if (events.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No events found in Firestore.", Toast.LENGTH_SHORT).show()
                } else {
                    val calendarId = getPrimaryCalendarId()
                    if (calendarId == null) {
                        Toast.makeText(this@MainActivity, "No accessible primary calendar found.", Toast.LENGTH_LONG).show()
                    } else {
                        insertEventsIntoCalendar(events, calendarId)
                        Toast.makeText(this@MainActivity, "Events added to your calendar!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private suspend fun fetchAllEventsFromFirestore(): List<EventData> = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val result = CompletableDeferred<List<EventData>>()
        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->
                val events = documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val description = doc.getString("description") ?: ""
                    val location = doc.getString("location") ?: ""
                    val timezone = doc.getString("timezone") ?: TimeZone.getDefault().id
                    val startTimestamp = doc.getTimestamp("startTime") ?: return@mapNotNull null
                    val endTimestamp = doc.getTimestamp("endTime") ?: return@mapNotNull null

                    EventData(
                        title = title,
                        description = description,
                        startTimeMillis = startTimestamp.toDate().time,
                        endTimeMillis = endTimestamp.toDate().time,
                        location = location,
                        timezone = timezone
                    )
                }
                result.complete(events)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching events", e)
                result.complete(emptyList())
            }

        return@withContext result.await()
    }

    private fun getPrimaryCalendarId(): Long? {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY)
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idIdx = cursor.getColumnIndex(CalendarContract.Calendars._ID)
            val primaryIdx = cursor.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY)
            while (cursor.moveToNext()) {
                val calId = cursor.getLong(idIdx)
                val isPrimary = if (primaryIdx >= 0) cursor.getInt(primaryIdx) == 1 else false
                if (isPrimary) {
                    return calId
                }
            }
        }

        contentResolver.query(uri, arrayOf(CalendarContract.Calendars._ID), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(0)
            }
        }

        return null
    }

    private fun insertEventsIntoCalendar(events: List<EventData>, calendarId: Long) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "No WRITE_CALENDAR permission.")
            return
        }

        for (event in events) {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, event.startTimeMillis)
                put(CalendarContract.Events.DTEND, event.endTimeMillis)
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, event.description)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, event.timezone)
                if (event.location.isNotEmpty()) {
                    put(CalendarContract.Events.EVENT_LOCATION, event.location)
                }
            }

            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }
    }
    private fun disableDownloadButton() {
        downloadButton.isEnabled = true // Keep the button enabled but non-functional
        downloadButton.setOnClickListener {
            showAccessDeniedDialog() // Show Access Denied dialog on click
        }
        downloadButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
    }


//    private fun disableDownloadButton() {
//        downloadButton.isEnabled = false
//        downloadButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
////        showAccessDeniedDialog() // Show Access Denied Dialog
//
//    }

    private fun savePermissionDeniedState(denied: Boolean) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(PREF_PERMISSION_DENIED, denied).apply()
    }

    private fun isPermissionDenied(): Boolean {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(PREF_PERMISSION_DENIED, false)
    }
    //
//    private fun updateButtonState() {
//        if (isPermissionDenied()) {
//            disableDownloadButton()
//        }
//    }
    private fun updateButtonState() {
        if (isPermissionDenied()) {
            disableDownloadButton() // Disable the button and set the click listener for the dialog
        } else {
            downloadButton.isEnabled = true
            downloadButton.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            downloadButton.setOnClickListener {
                if (isPermissionDenied()) {
                    showAccessDeniedDialog() // Just in case of a late change
                } else {
                    checkPermissionAndDownload() // Regular download logic
                }
            }
        }
    }

    private fun showAccessDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Access Denied")
            .setMessage("Unfortunately, we cannot provide full service to you right now. You can edit the permission later in settings.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
// Simple data class to hold event info
data class EventData(
    val title: String,
    val description: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val location: String,
    val timezone: String
)