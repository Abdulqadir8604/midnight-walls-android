package com.lamaq.midnight_walls;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.lamaq.midnight_walls.activities.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent( SplashScreenActivity.this, LoginActivity.class));
        }else{
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }
        finish();
    }
}
