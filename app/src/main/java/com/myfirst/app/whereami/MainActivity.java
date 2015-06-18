package com.myfirst.app.whereami;


import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private Location myLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
	private Button ShowLocation;
	private TextView LocationText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationText = (TextView) findViewById(R.id.LocationText);
        ShowLocation = (Button) findViewById(R.id.ShowLocation);

        // Check Whether google play services are available
        if(isSupportPlayService()){

            // Create a GoogleApiClient instance
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();

            //Create LocationRequest instance
            mLocationRequest = new LocationRequest();

            // NORMAL update time 20 sec
            mLocationRequest.setInterval(20000);

            // Fastest update time 5 sec
            mLocationRequest.setFastestInterval(5000);

            //Highest Accuracy
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //Update if user move >= 20 meters
            mLocationRequest.setSmallestDisplacement(10);
        }


        ShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getLocation();
                startLocationUpdates();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect to GoogleApiClient
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        isSupportPlayService();

        //  Periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect  GoogleApiClient
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop Updates
        stopLocationUpdates();
    }


    // Checking availability of google play services
    private boolean isSupportPlayService() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if ( status != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this,1).show();
            } else {
                Toast.makeText(getApplicationContext(),"Play Services not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    // Main Part of the Program i.e getting Location
    private void getLocation() {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if ( myLocation != null) {

            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LocationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
            //LocationText.setText(latitude + ", " + longitude);

        } else {
            //Log.d("e","dad");
            LocationText.setText("(Couldn't get the location. Make sure location service is enabled on the device)");
        }
    }



    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("TAG", "Connection failed: ErrorCode = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Already connected to GoogleApiClient so get the location
        getLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        //Connect to GoogleApiClient
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        // Assign the new location
        myLocation = location;

        // Display the new location
        getLocation();
    }

    // converting latitude, longitude to address
    private class GeocoderHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            String locationAddress;

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            LocationText.setText(locationAddress);
        }
    }


}

