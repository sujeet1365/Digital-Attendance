package com.kpf.sujeet.digitaleducation.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kpf.sujeet.digitaleducation.R;

import java.util.ArrayList;
import java.util.Iterator;

public class RegisterLocation extends Activity implements View.OnClickListener, LocationListener {

    Button btn_set_location;
    Button btn_location_done;
    Button btn_location_delete;
    Button btn_verify;
    double lattitude;
    double longitude;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    LocationManager locationManager;
    public  static ArrayList<String> lattitudeList,longitudeList;
    String district,block,nprc,school,timeStamp;
    int c=0,c1=0;
    ProgressDialog progressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    Activity activity;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_location);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btn_set_location = (Button)findViewById(R.id.btn_set_location);
        btn_location_done = (Button)findViewById(R.id.btn_location_done);
        btn_location_delete = (Button)findViewById(R.id.btn_location_delete);
        btn_verify = (Button)findViewById(R.id.btn_verify);

        btn_set_location.setOnClickListener(this);
        btn_location_done.setOnClickListener(this);
        btn_location_delete.setOnClickListener(this);
        btn_verify.setOnClickListener(this);

        lattitudeList = new ArrayList<String>();
        longitudeList = new ArrayList<String>();

        LocationListener locationListener = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)

            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }//else if(ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)){
//            Toast.makeText(this,"GPS permission allows us to access location data.",Toast.LENGTH_LONG).show();
//         } else {
//            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
//         }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        district = sharedPreferences.getString("district","");
        block = sharedPreferences.getString("block","");
        nprc = sharedPreferences.getString("nprc","");
        school = sharedPreferences.getString("school_name","");

        btn_location_delete.setEnabled(false);
    }

    @Override
    public void onClick(View view) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");

        switch (view.getId()){

            case R.id.btn_set_location :
                if (district.equals("") || block.equals("") || nprc.equals("") || school.equals("") || lattitude==0.0 || longitude==0.0){
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    setLocation();
                }

                break;
            case R.id.btn_verify:
                getLocation();
                break;

            case R.id.btn_location_done :
                progressDialog.show();
                if(lattitudeList.size()==longitudeList.size() && lattitudeList.size()>=3)
                    varification();
                else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Location can't be Varify", Toast.LENGTH_SHORT).show();
                }
//                finish();
                break;
            case R.id.btn_location_delete:
                progressDialog.show();
                deleteLastLocation();
        }

    }

    public void setLocation(){
        Long timeStampLong = System.currentTimeMillis()/1000;
        timeStamp = timeStampLong.toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("location").child(district).child(block).child(nprc).child(school);
        databaseReference.child("lattitude").child(timeStamp).setValue(Double.toString(lattitude)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.child("longitude").child(timeStamp).setValue(Double.toString(longitude));
            }
        });
        progressDialog.dismiss();
        Toast.makeText(this, "Location Set", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(RegisterLocation.this, "GPS is ON", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(RegisterLocation.this, "Please turn on your GPS ", Toast.LENGTH_SHORT).show();

    }

    public void getLocation(){
        progressDialog.show();
        lattitudeList.clear();
        longitudeList.clear();
        Query query = FirebaseDatabase.getInstance().getReference().child("location").child(district).child(block).child(nprc).child(school).child("lattitude");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot)iterator.next();
                    lattitudeList.add(snapshot.getValue().toString());//.substring(0,10));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query1 = FirebaseDatabase.getInstance().getReference().child("location").child(district).child(block).child(nprc).child(school).child("longitude");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot)iterator.next();
                    
                    longitudeList.add(snapshot.getValue().toString());//.substring(0,10));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void varification(){
        c=1;

        double lat1,lat2,long1,long2;

        double [] lattitudes = new double[lattitudeList.size()];
        for (int i = 0; i < lattitudeList.size(); ++i) {
            lattitudes[i] = Double.parseDouble(lattitudeList.get(i));
        }

        double [] longitudes = new double[longitudeList.size()];
        for (int i = 0; i < longitudeList.size(); ++i) {
            longitudes[i] = Double.parseDouble(longitudeList.get(i));
        }

        for(int i=1;i<lattitudes.length;i++){
            lat1 = lattitudes[0];
            long1 = longitudes[0];
            lat2 = lattitudes[i];
            long2 = longitudes[i];

            double differance = distFrom(lat1,long1,lat2,long2);
            if((differance<10)){
                c++;
                if(c==lattitudeList.size()){
                    SharedPreferences.Editor editor= getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("lat",lattitudeList.get(0));
                    editor.putString("long",longitudeList.get(0));
                    editor.commit();
                    Toast.makeText(this, "Location Varified "+differance, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,HomeActivity.class));
                    progressDialog.dismiss();
                    finish();
                }
            }else {
                Toast.makeText(this,differance+" m. Away From location", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                btn_location_delete.setEnabled(true);
            }
        }

}
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (earthRadius * c);

        return dist;
    }

    public  void deleteLastLocation(){

        Query queryDel1 = FirebaseDatabase.getInstance().getReference().child("location").child(district).child(block).child(nprc).child(school).child("lattitude");
        queryDel1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot)iterator.next();
                    if(snapshot.getValue().toString().equals(lattitudeList.get(lattitudeList.size()-1).toString())){
                        snapshot.getRef().removeValue();
                        Toast.makeText(RegisterLocation.this, "Deleted...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Delete", "onCancelled", databaseError.toException());
            }
        });

        Query queryDel2 = FirebaseDatabase.getInstance().getReference().child("location").child(district).child(block).child(nprc).child(school).child("longitude");
        queryDel2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot)iterator.next();
                    if(snapshot.getValue().toString().equals(longitudeList.get(longitudeList.size()-1).toString())){
                        snapshot.getRef().removeValue();
                        Toast.makeText(RegisterLocation.this, "Deleted...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Delete", "onCancelled", databaseError.toException());
            }
        });
    }

}
