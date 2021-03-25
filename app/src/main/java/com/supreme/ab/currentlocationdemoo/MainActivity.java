package com.supreme.ab.currentlocationdemoo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DocumentReference ref= FirebaseFirestore.getInstance().collection("Vehicles").document("Location");
    Button btlocation;
    List<Address> addresses;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btlocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);
        textView3 = findViewById(R.id.textview3);
        textView4 = findViewById(R.id.textview4);
        textView5 = findViewById(R.id.textview5);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                }
            }
        });
    }

    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                  if(location!=null){
                      try {
                          Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                          List<Address> addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);

                          //Latitude
                          textView1.setText(Html.fromHtml("<font color= '#6200EE'><b>Latitude :</b><br></font>" +
                                  addresses.get(0).getLatitude()
                          ));
                          //Longitude
                          textView2.setText(Html.fromHtml("<font color= '#6200EE'><b>Longitude :</b><br></font>" +
                                  addresses.get(0).getLongitude()
                          ));
                          //CountryName
                          textView3.setText(Html.fromHtml("<font color= '#6200EE'><b>Country :</b><br></font>" +
                                  addresses.get(0).getCountryName()
                          ));
                          //Locality
                          textView4.setText(Html.fromHtml("<font color= '#6200EE'><b>Locality :</b><br></font>" +
                                  addresses.get(0).getLocality()
                          ));
                          //AddressLine
                          textView5.setText(Html.fromHtml("<font color= '#6200EE'><b>Address :</b><br></font>" +
                                  addresses.get(0).getAddressLine(0)
                          ));



                      }
                      catch (IOException e){
                          e.printStackTrace();
                      }
                  }
            }
        });
    }
    public void saveLocation(View V){

        btlocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);
        textView3 = findViewById(R.id.textview3);
        textView4 = findViewById(R.id.textview4);
        textView5 = findViewById(R.id.textview5);


        String str= textView1.getText().toString();
        String str2= textView2.getText().toString();
        String str3= textView3.getText().toString();
        String str4= textView4.getText().toString();
        String str5= textView5.getText().toString();

        if (str.isEmpty()|| str2.isEmpty() || str3.isEmpty()|| str4.isEmpty() || str5.isEmpty()){ return;}

        Map<String, Object> dataToSave= new HashMap<String,Object>();

        dataToSave.put("Latitude", str);
        dataToSave.put("Longitude", str2);
        dataToSave.put("Country", str3);
        dataToSave.put("Locality", str4);
        dataToSave.put("Address", str5);

        ref.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("text","Saved");
                Toast.makeText(getApplicationContext(),"Saved On Server Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
