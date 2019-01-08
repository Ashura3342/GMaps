package fr.wildcodeschool.gmaps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback,
        GpsLocation.GpsListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final int FINE_LOCATION_REQUEST = 299;
    private static final int MIN_TIME = 0;
    private static final int MIN_DISTANCE = 0;
    private static final boolean USE_NETWORK = true;
    private static final String TAG = MapsActivity.class.getName();
    private GoogleMap mMap;
    private GpsLocation gps;
    private SupportMapFragment mapFragment;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        checkPermission();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Location location = gps.getLocation();
        if (location != null) {
            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
            // mMap.addMarker(new MarkerOptions().position(me).title("Me"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 15));

            LatLng center = new LatLng(44.837789f, -0.57918f);
            int radius = 1000;

            LatLngBounds bordeauxBox = new LatLngBounds.Builder()
                    .include(SphericalUtil.computeOffset(center, radius, 0))
                    .include(SphericalUtil.computeOffset(center, radius, 90))
                    .include(SphericalUtil.computeOffset(center, radius, 180))
                    .include(SphericalUtil.computeOffset(center, radius, 270))
                    .build();
            mMap.setLatLngBoundsForCameraTarget(bordeauxBox);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerClickListener(this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        if (null != marker) {
            marker.remove();
            marker = null;
        }
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Point"));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Uri gmmIntentUri = Uri.parse(String.format(Locale.getDefault(),
                "google.navigation:q=%f,%f&mode=b",
                marker.getPosition().latitude,
                marker.getPosition().longitude));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

        return false;
    }

    @Override
    public void onReady() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermission() {

        // vérification de l'autorisation d'accéder à la position GPS

        if (ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // l'autorisation n'est pas acceptée
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    FINE_LOCATION_REQUEST);
        } else {
            gps = new GpsLocation(this,
                    MIN_TIME, MIN_DISTANCE , USE_NETWORK, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (FINE_LOCATION_REQUEST == requestCode) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gps = new GpsLocation(this,
                        MIN_TIME, MIN_DISTANCE , USE_NETWORK, this);
            } else {
                finish();
            }
        }
    }
}
