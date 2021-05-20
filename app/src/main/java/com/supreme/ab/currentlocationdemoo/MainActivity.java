package com.supreme.ab.currentlocationdemoo;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private DocumentReference ref= FirebaseFirestore.getInstance().collection("Driver").document("Driver123");
    private final static String TAG="Main Activity3";
    TextView City, Country, DriverN, iddd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_new_content_twitter){

        }
        return super.onOptionsItemSelected(item);
    }

    public void startTracking(View view){
            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);

            }
                ComponentName componentName = new ComponentName(this, jobServices.class);
                JobInfo jobInfo= new JobInfo.Builder(123, componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(3*60*1000)   // interval for time sending
                        .build();

                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                int resultcode = jobScheduler.schedule(jobInfo);
                if(resultcode==JobScheduler.RESULT_SUCCESS){
                    Log.d(TAG, "Job Scheduled scheduled!");
                }
                else{
                    Log.d(TAG, "Job scheduling failed");
                }
                getLocation();

            }

    //    JobScheduler jobScheduler;

    protected void getLocation() {
        City = findViewById(R.id.city);
        Country = findViewById(R.id.country);
        DriverN= findViewById(R.id.driverName);
        iddd= findViewById(R.id.iddd);

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String country = documentSnapshot.getString("Country");
                    String city = documentSnapshot.getString("City");
                    String driverName= documentSnapshot.getString("Name");
                    String id= documentSnapshot.getString("DriverID");

                    City.setText(city);
                    Country.setText(country);
                    DriverN.setText(driverName);
                    iddd.setText(id);
                    Toast.makeText(getApplicationContext(),"Driver profile loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void stopTracking(View view){
        JobScheduler jobScheduler= (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(123);

    }
}
