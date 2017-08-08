package com.kpf.sujeet.digitaleducation.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kpf.sujeet.digitaleducation.Activity.HomeActivity;
import com.kpf.sujeet.digitaleducation.Activity.Register_Activity;
import com.kpf.sujeet.digitaleducation.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    View rootview;
    RadioButton present_radio_btn;
    RadioButton absent_radio_btn;
    TextView txtvw_no_of_students;
    EditText edt_student_no;
    Button btn_home_submit;
    FirebaseAuth auth;
    EditText edt_subject1;
    EditText edt_subject2;
    EditText edt_subject3;
    ImageView map;
    String district,block,nprc,name,designation,email,contact,school_name,uid,lat,lng;
    int count;
    DatabaseReference databaseReference;
    //    Spinner spinner_schools;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("data",MODE_PRIVATE);
        district = sharedPreferences.getString("district",null);
        block = sharedPreferences.getString("block",null);
        nprc = sharedPreferences.getString("nprc",null);
        school_name = sharedPreferences.getString("school_name",null);
        name = sharedPreferences.getString("name",null);
        lat = sharedPreferences.getString("lat",null);
        lng = sharedPreferences.getString("long",null);

        edt_subject1 = (EditText)rootview.findViewById(R.id.edt_subject1);
        edt_subject2 = (EditText)rootview.findViewById(R.id.edt_subject2);
        edt_subject3 = (EditText)rootview.findViewById(R.id.edt_subject3);
        map = (ImageView)rootview.findViewById(R.id.map);


        txtvw_no_of_students = (TextView)rootview.findViewById(R.id.txtvw_no_of_students);
        edt_student_no = (EditText)rootview.findViewById(R.id.edt_student_no);
        btn_home_submit = (Button)rootview.findViewById(R.id.btn_home_submit);

        present_radio_btn = (RadioButton)rootview.findViewById(R.id.present_radio_btn);
        absent_radio_btn = (RadioButton)rootview.findViewById(R.id.absent_radio_btn);

        present_radio_btn.setOnClickListener(this);
        absent_radio_btn.setOnClickListener(this);
        btn_home_submit.setOnClickListener(this);


//        spinner_schools.setEnabled(false);
        edt_student_no.setEnabled(false);
        btn_home_submit.setEnabled(false);
        edt_subject1.setEnabled(false);
        edt_subject2.setEnabled(false);
        edt_subject3.setEnabled(false);

        return rootview;
    }



    @Override
    public void onClick(View view) {

        Long timeStampLong = System.currentTimeMillis()/1000;
        final String timeStamp = timeStampLong.toString();

//        RadioButton radioButton = (RadioButton)view;
        switch (view.getId()){
            case R.id.present_radio_btn:
                if (present_radio_btn.isChecked()) {
//                    edt_student_no.setEnabled(true);
                    btn_home_submit.setEnabled(true);

                    Toast.makeText(getContext(), "Welcome "+auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.absent_radio_btn:
                if(absent_radio_btn.isChecked()){
                    Toast.makeText(getContext(), "Send your Reason", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_home_submit:
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Processing...");
                progressDialog.show();

                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String DateToStr = format.format(curDate);

                if(!lat.equals("") || !lng.equals("") || HomeActivity.lattitude!=0 || HomeActivity.longitude!=0){
                    double distance = distFrom(Double.parseDouble(lat),Double.parseDouble(lng),HomeActivity.lattitude,HomeActivity.longitude);
                    if(distance<=10){
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("attendance").child(district).child(block).child(nprc).child(school_name).child(DateToStr);
                        databaseReference.child(uid).setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getContext(), "Succesfully submitted", Toast.LENGTH_SHORT).show();
                                edt_student_no.setText("");
                                progressDialog.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "You are not in range", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }else{
                    Toast.makeText(getContext(), "Try Again !!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                String number = edt_student_no.getText().toString();
                break;
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
}
