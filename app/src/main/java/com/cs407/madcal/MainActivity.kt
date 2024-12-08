package com.cs407.madcal

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    private var job : Job? = null
    private lateinit var downloadButton : Button
    private lateinit var selectionButton : Button
    private val scope = CoroutineScope(Dispatchers.IO)

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

        // Optional: Request calendar permissions at runtime if needed.
        // Ensure you handle the result in onRequestPermissionsResult
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR), 100)
        }
    }

    fun startDownload(view: View) {
        // Show dialog prompting user
        AlertDialog.Builder(this)
            .setTitle("Download Events")
            .setMessage("If you want to download selected events, go to 'Selection'. Otherwise, we will download ALL recent events. Do you want to proceed?")
            .setPositiveButton("YES") { dialog, _ ->
                dialog.dismiss()
                // User chose yes, start download logic
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission denial gracefully
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

        // If no primary calendar, pick the first one available
        contentResolver.query(uri, arrayOf(CalendarContract.Calendars._ID), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(0)
            }
        }

        return null
    }

    private fun insertEventsIntoCalendar(events: List<EventData>, calendarId: Long) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
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