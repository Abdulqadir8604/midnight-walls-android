package com.lamaq.midnight_walls.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.lamaq.midnight_walls.R;

public class LoginActivity extends AppCompatActivity {

//    private CountryCodePicker ccp;
    private EditText number;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
//        ccp = findViewById(R.id.ccp);
        number = findViewById(R.id.editText_carrierNumber);
        next = findViewById(R.id.next);

//        ccp.registerCarrierNumberEditText(number);

        next.setOnClickListener(view -> {
            if (TextUtils.isEmpty(number.getText().toString())){
                Toast.makeText(LoginActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            }else if (number.getText().toString().replace(" ", "").length()!=10){
                Toast.makeText(LoginActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                intent.putExtra("phone_number", number.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
}