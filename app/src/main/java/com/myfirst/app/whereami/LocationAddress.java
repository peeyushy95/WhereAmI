package com.myfirst.app.whereami;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                    if (addressList != null && addressList.size() > 0) {

                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                sb.append(address.getAddressLine(i)).append("\n");
                        }

                        sb.append(address.getCountryName()).append("\n");;
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to  connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    if (result != null) {
                            result = "Latitude: " + latitude + "\nLongitude: " + longitude + "\n\nAddress:\n" + result;
                    }
                    else {
                            result = "Latitude: " + latitude + "\nLongitude: " + longitude + "\n Unable to get address for this lat-long.";
                    }
                    bundle.putString("address", result);
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}