package fr.wildcodeschool.gmaps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsLocation implements LocationListener {
    private LocationManager locationManager = null;
    private int minTime;
    private int minDistance;
    private boolean useNetwork;
    private GpsListener listener;
    private Location location = null;
    private boolean isFirstTime = true;

    public GpsLocation(Context context,
                       int minTime, int minDistance, boolean useNetwork, GpsListener listener) {
        this.locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        this.minTime = minTime;
        this.minDistance = minDistance;
        this.useNetwork = useNetwork;
        this.listener = listener;
        initLocation();
    }

    public int getMinTime() {
        return minTime;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public Location getLocation() {
        return location;
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        String provider = useNetwork ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (isFirstTime) {
            this.listener.onReady();
            isFirstTime = false;
        }
        this.listener.onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface GpsListener {
        void onReady();
        void onLocationChanged(Location location);
    }
}
