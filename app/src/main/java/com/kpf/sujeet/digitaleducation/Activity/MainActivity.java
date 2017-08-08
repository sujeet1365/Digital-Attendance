package com.kpf.sujeet.digitaleducation.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRouter;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.kpf.sujeet.digitaleducation.R;
import com.kpf.sujeet.digitaleducation.Activity.Register_Activity;

import java.util.Arrays;

public class MainActivity extends Activity implements View.OnClickListener {
    EditText edt_login_username;
    EditText edt_login_password;
    Button btn_login;
    Button btn_register;
    String email, password, name;
    ProgressDialog progressDialog;
    CallbackManager mCallbackManager;

    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth fbauth;
    private FirebaseAuth.AuthStateListener fbauthlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
        }
        setContentView(R.layout.activity_main);

        fbauth = FirebaseAuth.getInstance();
        fbauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // dialog.dismiss();
                // user = firebaseAuth.getCurrentUser();
            }
        };

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        edt_login_username = (EditText)findViewById(R.id.edt_login_username);
        edt_login_password = (EditText)findViewById(R.id.edt_login_password);

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        fbauth.addAuthStateListener(fbauthlistener);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Processing......");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fbauthlistener != null) {
            fbauth.removeAuthStateListener(fbauthlistener);
        }
    }

    @Override
    public void onClick(View view) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing......");

        email = edt_login_username.getText().toString().trim();
        password = edt_login_password.getText().toString().trim();
        switch (view.getId())
        {
            case R.id.btn_login:
                progressDialog.show();
                if(email.equals("")){
                    edt_login_username.setError("Empty");
                }else if(password.equals("")){
                    edt_login_password.setError("Empty");
                }else {
                    fbauth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Login", "signInWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        Log.w("Login", "signInWithEmail:failed", task.getException());
                                        Toast.makeText(MainActivity.this, R.string.authentication_failed,
                                                Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        progressDialog.dismiss();
                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
                break;
            case R.id.btn_signup:
                progressDialog.show();
                startActivity(new Intent(this,Register_Activity.class));
                finish();
                progressDialog.dismiss();
                break;
        }
    }
}
