package com.accident.warning.system.app.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.accident.warning.system.app.models.LocationModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.accident.warning.system.app.CustomApplication;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import kotlin.jvm.Synchronized;


//@Singleton
//@Inject
public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {
    private static final LocationManager LOCATION_MANAGER = new LocationManager();

    private Subject<Location> subject = PublishSubject.create();
    private GoogleApiClient googleApiClient = null;
    private AppCompatActivity activity = null;
    private Context appContext = null;
    private LocationRequest locationRequest = null;
    private Location lastLocation = null;
    private boolean isFreshLocation = true;
    private LocationListener locationListener = null;
    private int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private int REQUEST_CHECK_SETTINGS = 111;
    private int REQUEST_CHECK_PERMISSION = 222;

    // Location updates intervals in sec
    private int UPDATE_INTERVAL = 3000; // 3 sec
    private int FATEST_INTERVAL = 3000; // 3 sec
    private int DISPLACEMENT = 100; // 1 meters

    public interface LocationListener {
        void onLocationAvailable(LocationModel model);

        void onFail(Status status);

        enum Status {
            PERMISSION_DENIED, NO_PLAY_SERVICE, DENIED_LOCATION_SETTING
        }
    }

    public LocationManager() {
    }

    public static LocationManager getInstance() {
        return LOCATION_MANAGER;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void triggerLocation(AppCompatActivity activity, LocationListener locationListener) {
        this.locationListener = locationListener;
        appContext = CustomApplication.getApplication().getBaseContext();
        this.activity = activity;
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean requestPermission() {
        if (activity != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CHECK_PERMISSION
                );
            } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CHECK_PERMISSION
                );
            } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CHECK_PERMISSION
                );
            } else {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public void init() {
        if (checkPlayServices()) {
            if (Build.VERSION.SDK_INT >= 23 && requestPermission()) {
                connectToClient();
            } else {
                connectToClient();
            }
        } else {
            if (locationListener != null) {
                locationListener.onFail(LocationListener.Status.NO_PLAY_SERVICE);
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        Log.e("Location Manager", "Location Setting " + status.hasResolution());
        if (status.hasResolution()) {
            Toast.makeText(activity, "Please Enable the Location service ", Toast.LENGTH_SHORT).show();
            try {
                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }

        } else {
            getLocation();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Location Manager", "onConnected");
        createLocationRequest();
        checkLocationEnable();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Location Manager", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Location Manager", "onConnectionFailed ");
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        subject.onNext(location);
        Log.e("Location Manager", "onLocationChanged " + location.getLatitude() + " : " + location.getLongitude());
        if (locationListener != null) {
            LocationModel model = LocationUtils.getInstance().calculateSpeed(location);
            locationListener.onLocationAvailable(model);
        }
    }

    public void startLocationUpdates() {
        if (!checkPermission()) {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }
    }

    protected void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    public void getLocation() {
        if (isFreshLocation) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            startLocationUpdates();
        } else {
            if (!checkPermission()) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (lastLocation != null) {
                    double latitude = lastLocation.getLatitude();
                    double longitude = lastLocation.getLongitude();
                    Log.e("LAST Location ", latitude + " : " + longitude);
                    if (locationListener != null) {
                        LocationModel model = LocationUtils.getInstance().calculateSpeed(lastLocation);
                        locationListener.onLocationAvailable(model);
                    }
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    startLocationUpdates();
                }
            }
        }
    }

    public void stop() {
        locationListener = null;
        activity = null;
        stopLocationUpdates();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation();
            } else {

                if (locationListener != null) {
                    locationListener.onFail(LocationListener.Status.DENIED_LOCATION_SETTING);
                }
            }
        }
    }

    @Synchronized
    public void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity == null ? appContext : activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CHECK_PERMISSION) {
            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                connectToClient();
            } else {
                if (locationListener != null) {
                    locationListener.onFail(LocationListener.Status.PERMISSION_DENIED);
                }
            }
        }
    }

    private boolean checkPermission() {
        return activity != null && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPlayServices() {
        if (activity != null) {
            GoogleApiAvailability api = GoogleApiAvailability.getInstance();
            int result = api.isGooglePlayServicesAvailable(activity);
            if (result != ConnectionResult.SUCCESS) {
                if (api.isUserResolvableError(result)) {
                    api.getErrorDialog(activity, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void checkLocationEnable() {
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequestBuilder.build());
        result.setResultCallback(this);
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void connectToClient() {
        buildGoogleApiClient();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }
}