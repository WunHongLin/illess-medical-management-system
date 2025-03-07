package com.example.illess;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class illess_loginpage extends AppCompatActivity {

    private EditText ID,Password;
    private TextView buttonEntroll;
    private TextView buttonStart;
    private DocumentReference dbDR;
    private String DateToday;
    private Long currentCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_loginpage);

        buttonEntroll = (TextView) findViewById(R.id.buttonNotEntroll);
        buttonStart = (TextView) findViewById(R.id.ButtonStartUse);
        ID = (EditText) findViewById(R.id.LogInID);
        Password = (EditText) findViewById(R.id.LogInPassword);

        SharedPreferences preferences = getSharedPreferences("userFile",MODE_PRIVATE);
        ID.setText(preferences.getString("userID",""));
        Password.setText(preferences.getString("Password",""));

        buttonEntroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(illess_loginpage.this,illess_signuppage.class);
                claerEditText.launch(intent);
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(ID.getText().toString().isEmpty())){
                    //the ID is not empty
                    dbDR = FirebaseFirestore.getInstance().document("userInformation/"+ID.getText().toString());
                    dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                //ID is correct
                                if(!(Password.getText().toString().isEmpty())){
                                    //password is not empty
                                    DocumentSnapshot document = task.getResult();
                                    if(Password.getText().toString().equals(document.get("password"))){
                                        //classify sucess do the logIn event
                                        Intent intent = new Intent(illess_loginpage.this,illess_frontpage.class); //here need to go homePage
                                        intent.putExtra("userID",ID.getText().toString());
                                        intent.putExtra("userName",document.get("name").toString());

                                        //first clear the data in shared preference
                                        SharedPreferences ClearPreferences = getSharedPreferences("userFile",MODE_PRIVATE);
                                        ClearPreferences.edit().clear().commit();

                                        //set the data into sharedPreference
                                        SharedPreferences PutDataPreferences = getSharedPreferences("userFile",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = PutDataPreferences.edit();
                                        editor.putString("userID",ID.getText().toString());
                                        editor.putString("Password",Password.getText().toString());
                                        editor.commit();

                                        //first get the date today
                                        Calendar calender = Calendar.getInstance();
                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = calender.getTime();
                                        DateToday = simpleDate.format(date);

                                        SharedPreferences preferences = getSharedPreferences("userFile",MODE_PRIVATE);
                                        String userID = preferences.getString("userID","unknown");

                                        //second set the signup Event
                                        dbDR = FirebaseFirestore.getInstance().document("/userSignDate/"+userID+"/CurrentSignDate/"+DateToday);
                                        dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.getResult().exists()){
                                                    //check if the data exist
                                                }else{
                                                    signDate();
                                                }
                                            }
                                        });

                                        claerEditText.launch(intent);
                                    }else{
                                        // password is wrong
                                        Log.v("123","the password is wrong");
                                    }
                                }else{
                                    //password is empty
                                    Log.v("123","the password is empty");
                                }
                            }else{
                                //id is not exist
                                Log.v("123","the id is not exist");
                            }
                        }
                    });
                }else{
                    //the ID is empty
                    Log.v("123","the id is empty");
                }
            }
        });
    }

    public void signDate(){
        //first get the date today
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calender.getTime();
        DateToday = simpleDate.format(date);

        String[] arrayDate = DateToday.split("-");

        //put the data into HashMap
        HashMap<String,Integer> DateMap = new HashMap<String,Integer>();
        DateMap.put("year",Integer.parseInt(arrayDate[0]));
        DateMap.put("month",Integer.parseInt(arrayDate[1]));
        DateMap.put("day",Integer.parseInt(arrayDate[2]));

        //這裡需要將金額提升
        dbDR = FirebaseFirestore.getInstance().document("/userInformation/"+ID.getText().toString());
        dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                currentCoin = (Long) documentSnapshot.get("money");
                dbDR.update("money",currentCoin +10);

                dbDR = FirebaseFirestore.getInstance().document("/userSignDate/"+ID.getText().toString()+"/CurrentSignDate/"+DateToday);
                //put the DateMap to Database
                dbDR.set(DateMap);
            }
        });
    }

    private ActivityResultLauncher<Intent> claerEditText = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //clear the editText content
            ID.setText("");
            Password.setText("");
        }
    });
}