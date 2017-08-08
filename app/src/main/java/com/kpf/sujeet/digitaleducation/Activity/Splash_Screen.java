package com.kpf.sujeet.digitaleducation.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.kpf.sujeet.digitaleducation.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash_Screen extends Activity implements Animation.AnimationListener {

    private static int SPLASH_TIME_OUT = 3000;
    Animation animation,ani;
    ImageView img_kpf_icon;
    int flag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        img_kpf_icon = (ImageView) findViewById(R.id.img_kpf_icon);


//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.kpf.sujeet.digitaleducation",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(FirebaseAuth.getInstance().getCurrentUser() !=null){
                    Intent i = new Intent(Splash_Screen.this, HomeActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(Splash_Screen.this, MainActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);


        animation= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        ani=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        // set animation listener
        animation.setAnimationListener(this);
        ani.setAnimationListener(this);
        img_kpf_icon.startAnimation(animation);
    }
    @Override
    public void onAnimationEnd(Animation anim) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }


}
