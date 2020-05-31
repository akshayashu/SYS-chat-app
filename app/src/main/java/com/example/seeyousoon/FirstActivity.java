package com.example.seeyousoon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.seeyousoon.MessagesSection.BuddiesFragment;
import com.example.seeyousoon.MessagesSection.ContactsFragment;
import com.example.seeyousoon.MessagesSection.MessageMenuActivity;
import com.example.seeyousoon.data.ChatData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class FirstActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Location gps_loc = null, network_loc = null, final_loc = null;
    Double longitude = 0.0, latitude = 0.0;
    private String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mAuth = FirebaseAuth.getInstance();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            Toast.makeText(FirstActivity.this, "Permission not Granted.", Toast.LENGTH_SHORT).show();
            requestPermissions(permission,0);
        }else {
            callNextActivity();
        }
        try{
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(gps_loc != null ){
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }else if(network_loc != null){
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }else{
            latitude = 0.0;
            longitude = 0.0;
        }

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses != null && addresses.size() > 0){
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();

                final String MY_PREFS_NAME = "MyPrefsFile";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                if(state != "null" && country != "null") {
                    editor.putString("location", state + ", " + country);
                }
                editor.apply();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0 && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Granted permission", Toast.LENGTH_SHORT).show();
            callNextActivity();
        }
    }

    public void callNextActivity()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else {
                    Intent i = new Intent(FirstActivity.this, Signup.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);
    }

}
