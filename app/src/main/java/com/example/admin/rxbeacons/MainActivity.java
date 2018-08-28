package com.example.admin.rxbeacons;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivebeacons.library.rx2.Beacon;
import com.github.pwittchen.reactivebeacons.library.rx2.Proximity;
import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final boolean IS_AT_LEAST_ANDROID_M =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000;
    private static final String ITEM_FORMAT = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s";
    private ReactiveBeacons reactiveBeacons;
    private Disposable subscription;
    private ListView lvBeacons;
    private Map<String, Beacon> beacons;
    private Timer timer;
    Handler handler;
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    //private Button smsManagerBtn, getLocationBtn;
    //private TextView locationText;
    //private Button locBut;
    final String MAC_RPI = "b8:27:eb:bb:76:88";
    GPSTracker gps;
    private boolean hasStarted = false;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lvBeacons = (ListView) findViewById(R.id.lv_beacons);
        reactiveBeacons = new ReactiveBeacons(this);
        beacons = new HashMap<>();
        timer = new Timer();
        handler = new Handler();
        //check Permissions:
        if (!checkPermission(Manifest.permission.RECEIVE_SMS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 222);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override protected void onResume() {
        super.onResume();

        if (!canObserveBeacons()) {
            return;
        }

        startSubscription();
    }

    private void startSubscription() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestCoarseLocationPermission();
            return;
        }

        subscription = reactiveBeacons.observe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Beacon>() {
                    @Override public void accept(@NonNull Beacon beacon) throws Exception {
                        beacons.put(beacon.device.getAddress(), beacon);
                        //refreshBeaconList();
                    }
                });
    }

    private boolean canObserveBeacons() {
        if (!reactiveBeacons.isBleSupported()) {
            Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!reactiveBeacons.isBluetoothEnabled()) {
            reactiveBeacons.requestBluetoothAccess(this);
            return false;
        } else if (!reactiveBeacons.isLocationEnabled(this)) {
            reactiveBeacons.requestLocationAccess(this);
            return false;
        } else if (!isFineOrCoarseLocationPermissionGranted() && IS_AT_LEAST_ANDROID_M) {
            requestCoarseLocationPermission();
            return false;
        }

        return true;
    }

    private void refreshBeaconList() {


        List<String> list = new ArrayList<>();

        for (Beacon beacon : beacons.values()) {
            list.add(getBeaconItemString(beacon));
        }

        for (Beacon beacon : beacons.values()) {

            if(MAC_RPI.compareToIgnoreCase(beacon.device.getAddress())==0)
            {
                if(!hasStarted) {
                    hasStarted = true;
                    timer.schedule(SMStask, 0l, 1000 * 60 * 5);
                }
            }

        }

        int itemLayoutId = android.R.layout.simple_list_item_1;
        lvBeacons.setAdapter(new ArrayAdapter<>(this, itemLayoutId, list));

    }

    private String getBeaconItemString(Beacon beacon) {
        String mac = beacon.device.getAddress();
        int rssi = beacon.rssi;
        double distance = beacon.getDistance();
        Proximity proximity = beacon.getProximity();
        String name = beacon.device.getName();
        return String.format(ITEM_FORMAT, mac, rssi, distance, proximity, name);
    }


    @Override protected void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    //SMSTask
    public TimerTask SMStask = new TimerTask() {
        private int counter = 0;
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                    sendSMS();
                }
            });
            if(++counter == 3) {
                hasStarted = false;
                timer.cancel();
            }
        }
    };

    //SMS
    public void sendSMS()
    {
        String phoneNo = prefManager.getContactNumber();
        String sms = "Need assistance. Please follow the following link" + " " + getMapLink();
               /* if(getMapLink().equals(""))
                {
                    sms = "Need assistance. Please follow the following link"+getMapLink();
                }*/
        Toast.makeText(this, sms, Toast.LENGTH_LONG).show();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(this, "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    //GetLink
    public String getMapLink() {
        gps = new GPSTracker(this);
        String link = "";
        // check if GPS enabled
        if (gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            try {
                link = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }
        }        else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();

        }

        return link;
    }



    //Permission functions

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }
    @Override public void onRequestPermissionsResult(int requestCode,
                                                     @android.support.annotation.NonNull String[] permissions,
                                                     @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final boolean isCoarseLocation = requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION;
        final boolean permissionGranted = grantResults[0] == PERMISSION_GRANTED;

        //SMS permission
        if(requestCode == SEND_SMS_PERMISSION_REQUEST_CODE && grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
        {

        }

        if (isCoarseLocation && permissionGranted && subscription == null) {
            startSubscription();
        }


    }

    private void requestCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] { ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
    }

    private boolean isFineOrCoarseLocationPermissionGranted() {
        boolean isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        boolean isFineLocationPermissionGranted = isGranted(ACCESS_FINE_LOCATION);
        boolean isCoarseLocationPermissionGranted = isGranted(ACCESS_COARSE_LOCATION);

        return isAndroidMOrHigher && (isFineLocationPermissionGranted
                || isCoarseLocationPermissionGranted);
    }

    private boolean isGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;
    }

    public void openNewActivity(View view) {

        int id = view.getId();

        switch(id) {
            case (R.id.card1):
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                break;
            //case (R.id.card2):
                //intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);
                //break;
            case (R.id.card3):
                intent = new Intent(MainActivity.this, GuideActivity.class);
                startActivity(intent);
                break;
            case (R.id.card4):
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("About");
                builder.setIcon(R.drawable.ic_about);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(true);
                builder.setPositiveButton("Okay", null);

                AlertDialog alert = builder.create();
                alert.show();
                break;
            case (R.id.card5):
                intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                break;
        }

    }
}
