package com.example.illess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class dialogActivity extends AppCompatActivity {

    private TextView btnForget,btnRember,btnLater;
    private Button btnUpload,WhatMedicine,EatOrNot,buttonToPhoto;
    private String userID,currentTime,DateToday;
    private RecyclerView recycler;
    private DocumentReference dbDR;
    private CollectionReference dbCR,dbCR1;
    private dialogActivityAdapter adapter;
    private ArrayList<Map<String,String>> List = new ArrayList<Map<String,String>>();
    private ArrayList<MedicineInfo> MedicineList = new ArrayList<MedicineInfo>();
    private Calendar calender;
    private int minute,hour;
    private Dialog dialog;
    private View viewDialog;
    private Bitmap comparedImage;
    private int index;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        //get the id of user
        SharedPreferences preferences = getSharedPreferences("userFile",MODE_PRIVATE);
        userID = preferences.getString("userID","unknown");

        init();
    }

    public void init(){
        Calendar mCal = Calendar.getInstance();
        currentTime = (String) DateFormat.format("HH:mm", mCal.getTime());
        Log.v("123",currentTime);

        //find the views
        btnForget = (TextView) findViewById(R.id.textViewtoForgot);
        btnRember = (TextView) findViewById(R.id.textViewtoRemember);
        btnLater = (TextView) findViewById(R.id.textViewtoLater);
        buttonToPhoto = (Button) findViewById(R.id.buttonToPhotoPage1);

        //set the events
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(dialogActivity.this);
                viewDialog = getLayoutInflater().inflate(R.layout.pop_up9,null);
                dialog.setContentView(viewDialog);

                WhatMedicine = (Button) viewDialog.findViewById(R.id.WhatMedicine);
                EatOrNot = (Button) viewDialog.findViewById(R.id.EatOrNot);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WhatMedicine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setContentView(R.layout.medicine_search);
                        setFindPageContent();
                        buttonToPhoto = (Button) findViewById(R.id.buttonToPhotoPage1);
                        buttonToPhoto.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                });

                EatOrNot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setContentView(R.layout.medicine_search);
                        setFindPageContent();
                        buttonToPhoto = (Button) findViewById(R.id.buttonToPhotoPage1);
                        buttonToPhoto.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

                setRecord();

                //show the dialog
                dialog.show();
                dialog.getWindow().setLayout(850,1370);
            }
        });

        //set the remember
        btnRember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the store page
                finish();
                setRecord();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAlermData();
            }
        });
    }

    public void setFindPageContent(){
        List.clear();
        MedicineList.clear();

        //find the views
        recycler = (RecyclerView) findViewById(R.id.MedicineSearchRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(dialogActivity.this,LinearLayoutManager.HORIZONTAL,false));
        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

        String command = String.format("/userAlermTime/%s/AllTime",userID);
        dbCR = FirebaseFirestore.getInstance().collection(command);
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.get("time").equals(currentTime)){
                        Map<String,String> map = new HashMap<String,String>();
                        map.put("Name",documentSnapshot.get("name").toString());
                        map.put("Interval",documentSnapshot.get("interval").toString());
                        List.add(map);
                    }
                }
                for(index=0;index<List.size();index++){
                    String Name = List.get(index).get("Name");
                    String Interval = List.get(index).get("Interval");
                    String command1 = String.format("/userMedicineManage/%s/allIll/%s/%s",userID,Name,Interval);
                    dbCR1 = FirebaseFirestore.getInstance().collection(command1);
                    dbCR1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot:task.getResult()){
                                MedicineList.add(documentSnapshot.toObject(MedicineInfo.class));
                            }
                            if(index == List.size()){
                                adapter = new dialogActivityAdapter(MedicineList,dialogActivity.this);
                                recycler.setAdapter(adapter);
                                Log.v("123","456");
                            }
                            Log.v("123",String.valueOf(MedicineList.size()));
                        }
                    });
                }
            }
        });
    }

    private void setRecord(){
        List.clear();

        String command = String.format("/userAlermTime/%s/AllTime",userID);
        dbCR = FirebaseFirestore.getInstance().collection(command);
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.get("time").equals(currentTime)){
                        Map<String,String> map = new HashMap<String,String>();
                        map.put("Name",documentSnapshot.get("name").toString());
                        map.put("Interval",documentSnapshot.get("interval").toString());
                        List.add(map);
                    }
                }

                Calendar calender = Calendar.getInstance();
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                Date date = calender.getTime();
                DateToday = simpleDate.format(date);

                for(index=0;index<List.size();index++){
                    String Name = List.get(index).get("Name");
                    String Interval = List.get(index).get("Interval");
                    String commandRecord = String.format("/userMedicineRecord/%s/totalRecord/%s",userID,DateToday+Name+Interval);
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("Name",Name);
                    map.put("Interval",Interval);
                    map.put("time",DateToday);
                    dbDR = FirebaseFirestore.getInstance().document(commandRecord);
                    dbDR.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }
        });
    }

    private void resetAlermData(){
        String command = String.format("/userAlermTime/%s/AllTime",userID);
        dbCR = FirebaseFirestore.getInstance().collection(command);
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.get("time").equals(currentTime)){
                        Map<String,String> map = new HashMap<String,String>();
                        map.put("Name",documentSnapshot.get("name").toString());
                        map.put("Interval",documentSnapshot.get("interval").toString());
                        List.add(map);
                    }
                }

                //delete the data in database
                for(int index=0;index< List.size();index++){
                    String Name = List.get(index).get("Name");
                    String Interval = List.get(index).get("Interval");
                    String commandDelete = String.format("/userAlermTime/%s/AllTime/%s",userID,currentTime+Name+Interval);
                    dbDR = FirebaseFirestore.getInstance().document(commandDelete);
                    dbDR.delete();
                }

                //add new data in database
                for(int index=0;index< List.size();index++){
                    //get the current time
                    Calendar mCal = Calendar.getInstance();
                    currentTime = (String) DateFormat.format("HH:mm", mCal.getTime());
                    String[] array = currentTime.split(":");
                    minute = (Integer.parseInt(array[1]) + 5) % 60;
                    if(Integer.parseInt(array[1]) + 5 > 60){
                        hour = Integer.parseInt(array[0]) +1;
                    }else{
                        hour = Integer.parseInt(array[0]);
                    }

                    String Name = List.get(index).get("Name");
                    String Interval = List.get(index).get("Interval");
                    String updateTime = String.valueOf(hour)+":"+String.valueOf(String.format("%02d",minute));

                    Map<String,String> map = new HashMap<String,String>();
                    map.put("name",Name);
                    map.put("interval",Interval);
                    map.put("time",updateTime);

                    String commandAdd = String.format("/userAlermTime/%s/AllTime/%s",userID,updateTime+Name+Interval);
                    dbDR = FirebaseFirestore.getInstance().document(commandAdd);
                    dbDR.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            calender = Calendar.getInstance();
                            calender.set(Calendar.HOUR_OF_DAY,hour);
                            calender.set(Calendar.MINUTE,minute);
                            calender.set(Calendar.SECOND,0);
                            calender.set(Calendar.MILLISECOND,0);

                            //set alarm
                            alarmManager = (AlarmManager) dialogActivity.this.getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(dialogActivity.this,alermReceicer.class);
                            pendingIntent = PendingIntent.getBroadcast(dialogActivity.this,0,intent,0);
                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                            Toast.makeText(dialogActivity.this,"已經完成稍後提醒設定",Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            comparedImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(adapter.getUri()),null,null);
        } catch (FileNotFoundException e) {

        }
        //set the image of the view in medicineAdapter
        adapter.getImageView().setImageBitmap(comparedImage);

        //check the image is similar
        ImageView InitImage = (ImageView) findViewById(R.id.initialImage);
        BitmapDrawable drawable = (BitmapDrawable) InitImage.getDrawable();
        Bitmap InitMap = drawable.getBitmap();

        similarClass similar = new similarClass();
        TextView txtAlerm = findViewById(R.id.alermMessageText);

        if(similar.isMatch(InitMap,comparedImage)){
            if((1-similar.getAccuracy()) > 0.02){
                txtAlerm.setText("已經有吃藥了，記得上傳喔");
            }else{
                txtAlerm.setText("今天好像還沒吃藥喔，要記得吃喔");
            }
        }else{
            txtAlerm.setText("不是吃這個藥喔，重新拿一個吧");
        }
    }
}