package com.example.illess;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private TextView BtnLogInPage;
    private TextView BtnEntrollPage;

    static{
        if(OpenCVLoader.initDebug()){
            Log.v("123","Load Successfully");
        }else{
            Log.v("123","Load----");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnLogInPage = (TextView) findViewById(R.id.GoToLogInPage);
        BtnEntrollPage = (TextView) findViewById(R.id.GoToEntrollPage);

        BtnLogInPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,illess_loginpage.class);
                GoToPage.launch(intent);
            }
        });

        BtnEntrollPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,illess_signuppage.class);
                GoToPage.launch(intent);
            }
        });
    }

    private ActivityResultLauncher<Intent> GoToPage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //do-nothing
        }
    });
}