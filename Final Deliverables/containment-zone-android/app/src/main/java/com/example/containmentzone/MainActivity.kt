package com.example.finalgeofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback,
    OnMapClickListener, OnMarkerClickListener, ResultCallback<Status> {
    private var map: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var lastLocation: Location? = null
    private var textLat: TextView? = null
    private var textLong: TextView? = null
    private var mapFragment: MapFragment? = null
    private val KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE"
    private val KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textLat = findViewById<View>(R.id.lat) as TextView
        textLong = findViewById<View>(R.id.lon) as TextView

        // initialize GoogleMaps
        initGMaps()

        // create GoogleApiClient
        createGoogleApi()
    }

    private fun createGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }

    override fun onStart() {
        super.onStart()
        googleApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient!!.disconnect()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.geofence -> {
                startGeofence()
                return true
            }
            R.id.clear -> {
                clearGeofence()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val REQ_PERMISSION = 999

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQ_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_PERMISSION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    lastKnownLocation
                }
            }
        }
    }


    // Initialize GoogleMaps
    private fun initGMaps() {
        mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment!!.getMapAsync(this)
    }

    // Callback called when Map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setOnMapClickListener(this)
        map!!.setOnMarkerClickListener(this)
    }

    override fun onMapClick(latLng: LatLng) {
        markerForGeofence(latLng)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    private var locationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = 1000
    private val FASTEST_INTERVAL = 900

    // Start location Updates
    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL.toLong())
            .setFastestInterval(FASTEST_INTERVAL.toLong())
        if (checkPermission()) LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient!!, locationRequest!!
        ) { location: Location -> this.onLocationChanged(location) }
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        writeActualLocation(location)
    }

    override fun onConnected(bundle: Bundle?) {
        lastKnownLocation
        recoverGeofenceMarker()
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    private val lastKnownLocation: Unit
        private get() {
            if (checkPermission()) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient!!)
                if (lastLocation != null) {
                    writeLastLocation()
                    startLocationUpdates()
                } else {
                    startLocationUpdates()
                }
            } else askPermission()
        }

    private fun writeActualLocation(location: Location?) {
        textLat!!.text = "Lat: " + location!!.latitude
        textLong!!.text = "Long: " + location.longitude
        markerLocation(LatLng(location.latitude, location.longitude))
    }

    private fun writeLastLocation() {
        writeActualLocation(lastLocation)
    }

    private var locationMarker: Marker? = null
    private fun markerLocation(latLng: LatLng) {
        Log.i(TAG, "markerLocation($latLng)")
        val title = latLng.latitude.toString() + ", " + latLng.longitude
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(title)
        if (map != null) {
            if (locationMarker != null) locationMarker!!.remove()
            locationMarker = map!!.addMarker(markerOptions)
            val zoom = 14f
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
            map!!.animateCamera(cameraUpdate)
        }
    }

    private var geoFenceMarker: Marker? = null
    private fun markerForGeofence(latLng: LatLng) {
        val title = latLng.latitude.toString() + ", " + latLng.longitude
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .title(title)
        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null) geoFenceMarker!!.remove()
            geoFenceMarker = map!!.addMarker(markerOptions)
        }
    }

    // Start Geofence creation process
    private fun startGeofence() {
        if (geoFenceMarker != null) {
            val geofence = createGeofence(geoFenceMarker!!.position, GEOFENCE_RADIUS)
            val geofenceRequest = createGeofenceRequest(geofence)
            addGeofence(geofenceRequest)
        } else {
            Log.e(TAG, "Geofence marker is null")
        }
    }

    // Create a Geofence
    private fun createGeofence(latLng: LatLng, radius: Float): Geofence {
        return Geofence.Builder()
            .setRequestId(GEOFENCE_REQ_ID)
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setExpirationDuration(GEO_DURATION)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER
                        or Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()
    }

    // Create a Geofence Request
    private fun createGeofenceRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    private val geoFencePendingIntent: PendingIntent? = null
    private val GEOFENCE_REQ_CODE = 0
    private fun createGeofencePendingIntent(): PendingIntent {
        if (geoFencePendingIntent != null) return geoFencePendingIntent
        val intent = Intent(this, GeofenceTrasitionService::class.java)
        return PendingIntent.getService(
            this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private fun addGeofence(request: GeofencingRequest) {
        if (checkPermission()) LocationServices.GeofencingApi.addGeofences(
            googleApiClient!!,
            request,
            createGeofencePendingIntent()
        ).setResultCallback(this)
    }

    override fun onResult(status: Status) {
        Log.i(TAG, "onResult: $status")
        if (status.isSuccess) {
            saveGeofence()
            drawGeofence()
        } else {
            Log.i(TAG, "FAILED: ")
        }
    }

    // Draw Geofence circle on GoogleMap
    private var geoFenceLimits: Circle? = null
    private fun drawGeofence() {
        if (geoFenceLimits != null) geoFenceLimits!!.remove()
        val circleOptions = CircleOptions()
            .center(geoFenceMarker!!.position)
            .strokeColor(Color.argb(50, 70, 70, 70))
            .fillColor(Color.argb(100, 150, 150, 150))
            .radius(GEOFENCE_RADIUS.toDouble())
        geoFenceLimits = map!!.addCircle(circleOptions)
    }

    private fun saveGeofence() {
        val sharedPref = getPreferences(MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(
            KEY_GEOFENCE_LAT,
            java.lang.Double.doubleToRawLongBits(geoFenceMarker!!.position.latitude)
        )
        editor.putLong(
            KEY_GEOFENCE_LON,
            java.lang.Double.doubleToRawLongBits(geoFenceMarker!!.position.longitude)
        )
        editor.apply()
    }

    // Recovering last Geofence marker
    private fun recoverGeofenceMarker() {
        val sharedPref = getPreferences(MODE_PRIVATE)
        if (sharedPref.contains(KEY_GEOFENCE_LAT) && sharedPref.contains(KEY_GEOFENCE_LON)) {
            val lat = java.lang.Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LAT, -1))
            val lon = java.lang.Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LON, -1))
            val latLng = LatLng(lat, lon)
            markerForGeofence(latLng)
            drawGeofence()
        }
    }

    private fun clearGeofence() {
        LocationServices.GeofencingApi.removeGeofences(
            googleApiClient!!,
            createGeofencePendingIntent()
        ).setResultCallback { status ->
            if (status.isSuccess) {
                removeGeofenceDraw()
            }
        }
    }

    private fun removeGeofenceDraw() {
        if (geoFenceMarker != null) geoFenceMarker!!.remove()
        if (geoFenceLimits != null) geoFenceLimits!!.remove()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val NOTIFICATION_MSG = "NOTIFICATION MSG"

        fun makeNotificationIntent(context: Context?, msg: String?): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(NOTIFICATION_MSG, msg)
            return intent
        }

        private const val GEO_DURATION = (60 * 60 * 1000).toLong()
        private const val GEOFENCE_REQ_ID = "My Geofence"
        private const val GEOFENCE_RADIUS = 500.0f // in meters
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}