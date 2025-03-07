package com.example.illess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;


public class illess_calender extends AppCompatActivity {

    private Button dateSignTurnBack;
    private TextView dateSignButtonSign,calenderTitle,Coin;
    private MCalendarView Mcalender;
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private TextToSpeech speechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_calender);

        //init all views
        init();
    }

    //init all views and event of the illess_calender
    private void init(){
        //get the TurnBack Button
        dateSignTurnBack = (Button) findViewById(R.id.dateSignTurnBack);
        //set the event
        dateSignTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //mark the calender signDate
        markDate();

        Coin = (TextView) findViewById(R.id.NumberOfCoin);

        dbDR = FirebaseFirestore.getInstance().document("/userInformation/"+getIntent().getExtras().getString("userID"));
        dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Coin.setText(String.valueOf(documentSnapshot.get("money")));
            }
        });
    }

    private void markDate(){
        //get the view of clender
        Mcalender = (MCalendarView) findViewById(R.id.calender);

        //set the style of the calender
        Mcalender.hasTitle(false);

        //get the data from the database
        dbCR = FirebaseFirestore.getInstance().collection("/userSignDate/"+getIntent().getExtras().getString("userID")+"/CurrentSignDate");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document:task.getResult()){
                    //get the info of date divide
                    Long Year = (Long)document.get("year");
                    Long Month = (Long)document.get("month");
                    Long Day = (Long)document.get("day");

                    //set the title
                    calenderTitle = (TextView) findViewById(R.id.calenderTitle);
                    calenderTitle.setText(Integer.toString(Month.intValue())+"月"+Integer.toString(Year.intValue()));

                    //create a temp data and mark it
                    DateData date = new DateData(Year.intValue(), Month.intValue(), Day.intValue());
                    Mcalender.markDate(date);
                }
            }
        });

        //set the scroll event
        Mcalender.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                //set the Title
                calenderTitle.setText(Integer.toString(month)+"月"+Integer.toString(year));
            }
        });
    }
}