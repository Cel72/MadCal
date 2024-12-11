package com.cs407.madcal

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cs407.madcal.databinding.AtyMainBinding
import com.cs407.madcal.ui.main.AtyActivity
import com.cs407.madcal.ui.CalendarActivity
import com.cs407.madcal.ui.main.setting.SettingsActivity
import com.cs407.madcal.ui.main.setting.SettingsFragment
import com.cs407.madcal.utils.ActivityUtils.startActivity
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

        selectionButton.setOnClickListener {
            this@MainActivity.startActivity<AtyActivity>()
            finish()
        }

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

        val calendarIcon: ImageView = findViewById(R.id.calendar_icon)
        val settingsIcon: ImageView = findViewById(R.id.settings_icon)

        calendarIcon.setOnClickListener {
            // This will open your CalendarActivity
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        // Settings icon opens SettingsActivity (full screen)
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if the user has granted permissions after going to system settings
        val writeCalendarGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        if (writeCalendarGranted && isPermissionDenied()) {
            // The user previously denied permission, but now it's granted.
            savePermissionDeniedState(false)
        }

        // Update button state after checking the updated permission
        updateButtonState()
    }

    private fun checkPermissionAndDownload() {
        // Check if permission has been explicitly denied
        if (isPermissionDenied()) {
            // If permission was previously denied, disable the button and show a message
            disableDownloadButton()
            Toast.makeText(this, "Permission denied. Cannot download events.", Toast.LENGTH_SHORT).show()
        } else {
            // Check if the calendar permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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

    private suspend fun fetchAllEventsFromFirestore(): List<EventData> =
        withContext(Dispatchers.IO) {
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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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

        contentResolver.query(uri, arrayOf(CalendarContract.Calendars._ID), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getLong(0)
                }
            }

        return null
    }

    private fun insertEventsIntoCalendar(events: List<EventData>, calendarId: Long) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
            showAccessDeniedDialog()
        }
        downloadButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
    }

    private fun savePermissionDeniedState(denied: Boolean) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(PREF_PERMISSION_DENIED, denied).apply()
    }

    private fun isPermissionDenied(): Boolean {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(PREF_PERMISSION_DENIED, false)
    }

    private fun updateButtonState() {
        if (isPermissionDenied()) {
            disableDownloadButton()
        } else {
            downloadButton.isEnabled = true
            downloadButton.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            downloadButton.setOnClickListener {
                if (isPermissionDenied()) {
                    showAccessDeniedDialog()
                } else {
                    checkPermissionAndDownload()
                }
            }
        }
    }

    private fun showAccessDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Access Denied")
            .setMessage(
                "Unfortunately, we cannot provide full service to you right now. \n" +
                        "You can edit the permission later in your phone’s system setting by searching \"MadCal\"."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSettingsFragment() {
        // If ever needed, you still have the option to show the SettingsFragment within this activity:
        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
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
