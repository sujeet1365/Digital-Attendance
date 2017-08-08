package com.kpf.sujeet.digitaleducation.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kpf.sujeet.digitaleducation.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Register_Activity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_signup_name;
    EditText edt_signup_phone;
    EditText edt_signup_email;
    EditText edt_signup_password;
    EditText edt_signup_cnfrm_password;
    EditText edt_Designation;
    Button btn_signin;
    Button btn_register;
    DatabaseReference databaseReference;

    TextView textViews[] = new TextView[4];
    int id[] = {R.id.edt_District_name,R.id.edt_block_name,R.id.edt_nprc,R.id.edt_signup_school_name};

    Spinner spinner[]=new Spinner[4];
    int[] spinner_id={R.id.spinner_districts,R.id.spinner_block,R.id.spinner_nprc,R.id.spinner_school};

    ArrayList<String> arrayList;
    int n = 0;
    String data = "district";
    int count=0;



    private FirebaseAuth fbauth;
    private FirebaseAuth.AuthStateListener fbauthlistener;

    public static String district,block,nprc,name,designation,email,contact,school_name,password,cnfrm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);



        databaseReference = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < 4; i++) {
            textViews[i] = (TextView) findViewById(id[i]);
            spinner[i] = (Spinner) findViewById(spinner_id[i]);
        }

        arrayList = new ArrayList<String>();

        edt_signup_name = (EditText)findViewById(R.id.edt_signup_name);
        edt_signup_phone = (EditText)findViewById(R.id.edt_signup_contact);
        edt_Designation = (EditText)findViewById(R.id.edt_Designation);
        edt_signup_email = (EditText)findViewById(R.id.edt_signup_email);
        edt_signup_password = (EditText)findViewById(R.id.edt_signup_password);
        edt_signup_cnfrm_password = (EditText)findViewById(R.id.edt_signup_cnfirm_password);


        btn_signin = (Button)findViewById(R.id.btn_signin);
        btn_register = (Button)findViewById(R.id.btn_register);

        btn_signin.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        fbauth = FirebaseAuth.getInstance();

        fbauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Signup", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Signup", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        for(int j=0;j<4;j++){
            spinner[j].setEnabled(false);
        }
    }




    @Override
    public void onClick(View view) {
        district = textViews[0].getText().toString().trim();
        block = textViews[1].getText().toString().trim();
        nprc = textViews[2].getText().toString().trim();
        school_name = textViews[3].getText().toString().trim();
        name = edt_signup_name.getText().toString().trim();
        contact = edt_signup_phone.getText().toString().trim();
        designation = edt_Designation.getText().toString().trim();
        email = edt_signup_email.getText().toString().trim();
        password = edt_signup_password.getText().toString().trim();
        cnfrm_password = edt_signup_cnfrm_password.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_signin:
                if (email.equals(""))
                    edt_signup_email.setError("Empty Field");
                else if (edt_signup_password.equals(""))
                    edt_signup_password.setError("Empty Field");
                else if (cnfrm_password.equals(""))
                    edt_signup_cnfrm_password.setError("Empty Field");
                else if (!password.equals(cnfrm_password))
                    edt_signup_cnfrm_password.setError("No match found");
                else {
                    createUser(email, password);

                }
                break;

            case R.id.btn_register:
                if (district.equals(""))
                    textViews[0].setError("Empty Field");
                else if (block.equals(""))
                    textViews[1].setError("Empty Field");
                else if (nprc.equals(""))
                    textViews[2].setError("Empty Field");
                else if (school_name.equals(""))
                    textViews[3].setError("Empty Field");
                else if (name.equals(""))
                    edt_signup_name.setError("Empty Field");
                else if (contact.equals(""))
                    edt_signup_phone.setError("Empty Field");
                else if (designation.equals(""))
                    edt_Designation.setError("Empty Field");
                else {
                    saveUserData(FirebaseAuth.getInstance().getCurrentUser(),name,email,contact,district,block,nprc,school_name,designation);
                }
        }
    }

    public void createUser(final String email, final String password)
    {
        final ProgressDialog progressDialog = new ProgressDialog(Register_Activity.this);
        progressDialog.setMessage("Processing......");
        progressDialog.show();
        fbauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Signup", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(Register_Activity.this,"Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        else {

                            fbauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(!task.isSuccessful()){
                                        Toast.makeText(Register_Activity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }else{
                                        progressDialog.dismiss();
                                        for(int j=0;j<4;j++){
                                            spinner[j].setEnabled(true);
                                        }
                                        btn_signin.setEnabled(false);
                                        Toast.makeText(Register_Activity.this, "SingUp Successful", Toast.LENGTH_SHORT).show();

                                        get(n,data);

                                    }

                                }
                            });

                        }
                    }
                });
    }

    public int get(int n,String string) {

        count = n;
        arrayList.clear();

        Query query = FirebaseDatabase.getInstance().getReference().child(string);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                    arrayList.add(dataSnapshot1.getValue().toString());
                }
                Toast.makeText(Register_Activity.this, "Now Select : "/*+array[0]*/, Toast.LENGTH_SHORT).show();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Register_Activity.this, android.R.layout.simple_spinner_item, arrayList);
                spinner[count].setAdapter(adapter);
                spinner[count].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register_Activity.this);
                        if (position != 0) {
                            data = spinner[count].getSelectedItem().toString();

                            alertDialogBuilder.setMessage("Are you sure: " + data);
                            alertDialogBuilder.setPositiveButton("yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            textViews[count].setText(data);
                                            spinner[count].setEnabled(false);
                                            count++;
                                            if (count < 4) {
                                                get(count, data);
                                            }
                                        }

                                    });

                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         return 0;
    }

    public void saveUserData(FirebaseUser firebaseUser,String name,final String email,final String contact,final String district,final String block,final String nprc,final String school_name,final String designation){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = firebaseUser.getUid();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(district).child(block).child(nprc).child(school_name).child(uid);
        databaseReference.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.child("contact").setValue(contact);
                databaseReference.child("designation").setValue(designation);
                databaseReference.child("email").setValue(email);
            }
        });
        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor sp_editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        sp_editor.putString("district",district);
        sp_editor.putString("block",block);
        sp_editor.putString("nprc",nprc);
        sp_editor.putString("school_name",school_name);
        sp_editor.putString("name",name);
        sp_editor.commit();
        startActivity(new Intent(this,RegisterLocation.class));
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        fbauth.addAuthStateListener(fbauthlistener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fbauthlistener != null) {
            fbauth.removeAuthStateListener(fbauthlistener);
        }
    }
}

