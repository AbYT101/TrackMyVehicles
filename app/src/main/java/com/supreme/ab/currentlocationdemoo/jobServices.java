package com.supreme.ab.currentlocationdemoo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class jobServices extends JobService {

    private DocumentReference ref;
    private static final String TAG= "ExampleJobService";
    private boolean jobCancelled = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    String Latitude, Longitude, CountryName, Locality, AddressLine;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"job started");
        doBackgroundWork(params);
        return true;
    }
    private void doBackgroundWork(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Get Location
                getLocation(params);

                Log.d(TAG, "Job Finished");
                jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"Job Cancelled before completion");
        jobCancelled=true;
        return true;
    }

    private void getLocation(JobParameters params) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){
                    try {
                        Geocoder geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses= geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);

                        //Latitude
                        Latitude = addresses.get(0).getLatitude()+"";

                        //Longitude
                        Longitude= addresses.get(0).getLongitude()+"";
                        //CountryName
                        CountryName = addresses.get(0).getCountryName()+"";
                        //Locality
                        Locality= addresses.get(0).getLocality()+"";
                        //AddressLine
                        AddressLine = addresses.get(0).getAddressLine(0)+"";

                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }

                Log.d(TAG,Longitude+ " "+ Latitude);

                saveLocation();
            }
        });
    }
    private void saveLocation(){
//       if (Longitude.isEmpty()|| Latitude.isEmpty() || CountryName.isEmpty()|| Locality.isEmpty() || AddressLine.isEmpty()){ return;}

        //Build data for address
        Map<String, Object> dataToSave= new HashMap<String,Object>();

        dataToSave.put("Country", CountryName);
        dataToSave.put("Locality", Locality);
        dataToSave.put("Address", AddressLine);
        dataToSave.put("Latitude", Latitude);
        dataToSave.put("Longitude", Longitude);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currenttime= formatter.format(date);
        //Save data of address
        ref = FirebaseFirestore.getInstance().collection("Vehicle1 Location").document(currenttime);
        ref.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Location","Saved");
                Toast.makeText(getApplicationContext(),"Location Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
