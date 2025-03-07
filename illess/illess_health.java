package com.example.illess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
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
import java.util.Map;

public class illess_health extends AppCompatActivity {

    private Button buttonTurnBack,buttonAddIll,healthDetailTurnBack,healthDetailSearchButton,healthCreateRemind;
    private TextView dialogButtonAdd,dialogButtonClose,healthIllName,healthAddValue,dialogAddValueTitle,dialogButtonAddValue,dialogButtonAddClose,healthWatchMore,healthDetailTitle;
    private EditText dialogAddEditText;
    private Spinner detailMonth,detailYear;
    private LineChart healthIllChart;
    private RecyclerView recycler,healthDetailRecycler;
    private healthAdapter adapter;
    private healthDetailAdapter detailAdapter;
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private ArrayList<String> illName = new ArrayList<String>();
    private ArrayList<String> yearList,monthList;
    private ArrayList<healthInfoValue> chartData = new ArrayList<healthInfoValue>();
    private ArrayList<HashMap<String,String>> healthDetailMap = new ArrayList<HashMap<String,String>>();
    private Dialog dialog;
    private View viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_health);

        initTheState();
    }

    private void notifyTheDataChange(String ID){
        dbCR = FirebaseFirestore.getInstance().collection("userHealthInfo/"+ID+"/illName");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                illName.clear();
                adapter.notifyDataSetChanged();
                for(DocumentSnapshot documentSnapshot:task.getResult()){
                    illName.add(documentSnapshot.getId());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void initTheState(){
        buttonTurnBack = (Button) findViewById(R.id.healthDetailButtonTurnBack);
        buttonAddIll = (Button) findViewById(R.id.healthButtonAdd);
        healthAddValue = (TextView) findViewById(R.id.healthAddValue);
        healthWatchMore = (TextView) findViewById(R.id.healthWatchMore);
        healthDetailTitle = (TextView) findViewById(R.id.healthDetailTitle);

        //the addill event
        buttonAddIll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add the dialog
                dialog = new Dialog(illess_health.this);
                viewDialog = getLayoutInflater().inflate(R.layout.pop_up,null);
                dialog.setContentView(viewDialog);

                //add the button event
                dialogButtonAdd = viewDialog.findViewById(R.id.dialogAddIllName);
                dialogButtonClose = viewDialog.findViewById(R.id.dialogClose);
                dialogAddEditText = viewDialog.findViewById(R.id.dialogNameEditText);

                //dialog show
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(900,1050);

                //the addButton Event
                dialogButtonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dialogAddEditText.getText().toString().isEmpty()){
                            Toast.makeText(illess_health.this,"欄位尚未填寫",Toast.LENGTH_LONG).show();
                        }else{
                            String userID = getIntent().getExtras().getString("userID");
                            dbDR = FirebaseFirestore.getInstance().document("/userHealthInfo/"+userID+"/illName/"+dialogAddEditText.getText().toString());
                            Map<String,Object> emptyMap = new HashMap<String,Object>();
                            dbDR.set(emptyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // add success close the dialog
                                    dialog.dismiss();
                                    notifyTheDataChange(userID);
                                }
                            });
                        }
                    }
                });

                //the close event
                dialogButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        //the turnback Event
        buttonTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        illName.clear();

        //download the recycler info
        recycler = (RecyclerView) findViewById(R.id.healthRecycler);
        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recycler.setLayoutManager(new LinearLayoutManager(illess_health.this,LinearLayoutManager.HORIZONTAL,false));

        //get the data from database
        String userID = getIntent().getExtras().getString("userID");
        dbCR = FirebaseFirestore.getInstance().collection("userHealthInfo/"+userID+"/illName");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot:task.getResult()){
                    illName.add(documentSnapshot.getId());
                }
                healthIllName = (TextView) findViewById(R.id.healthIllName);
                healthIllChart = (LineChart) findViewById(R.id.healthIllChart);
                adapter = new healthAdapter(illName,healthIllName,healthIllChart,userID,illess_health.this);
                recycler.setAdapter(adapter);
            }
        });
    }
}