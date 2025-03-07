package com.example.illess;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class timeIllAdapter extends RecyclerView.Adapter<timeIllAdapter.ViewHolder>{

    private Button TurnBack,AddTime,moring,noon,night,bed,OK,Cancel;
    private TextView Title;
    private RecyclerView recycler;
    private ArrayList<String> NameList;
    private ArrayList<String> TimeList = new ArrayList<String>();
    private ArrayList<String> SelectTimeList = new ArrayList<String>();
    private illess_time Time;
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private timeDetailAdapter adapter;
    private int hour,minute;
    private Calendar calender;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Dialog dialog;
    private View viewDialog;
    private String selectTime="";
    private TimePickerDialog timePickerDialog;

    public timeIllAdapter(ArrayList<String> nameList, illess_time time) {
        NameList = nameList;
        Time = time;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item13,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(NameList.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change context
                Time.setContentView(R.layout.time_detail);
                createNotificationChannel();
                timeDetailInit(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return NameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.timeIllName);
            view = itemView;
        }
    }

    private void timeDetailInit(ViewHolder holder){
        //find the views
        TurnBack = (Button) Time.findViewById(R.id.timeDetailTurnBack);
        AddTime = (Button) Time.findViewById(R.id.timeDetailAddTime);
        Title = (TextView) Time.findViewById(R.id.timeDeatilTitle);
        recycler = (RecyclerView) Time.findViewById(R.id.timeDetailRecycler);

        //set the events
        TurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Time.setContentView(R.layout.activity_illess_time);
                Time.init();
            }
        });

        //set the addTime event
        AddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call the dialog
                dialog = new Dialog(Time);
                viewDialog = Time.getLayoutInflater().inflate(R.layout.pop_up10,null);
                dialog.setContentView(viewDialog);

                moring = (Button) viewDialog.findViewById(R.id.buttonMorning1);
                noon = (Button) viewDialog.findViewById(R.id.buttonNoon1);
                night = (Button) viewDialog.findViewById(R.id.buttonNight1);
                bed = (Button) viewDialog.findViewById(R.id.buttonBed1);
                OK = (Button) viewDialog.findViewById(R.id.buttonOK);
                Cancel = (Button) viewDialog.findViewById(R.id.buttonCancel);

                moring.setOnClickListener(buttonClickEvent);
                noon.setOnClickListener(buttonClickEvent);
                night.setOnClickListener(buttonClickEvent);
                bed.setOnClickListener(buttonClickEvent);

                OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectTime.equals("")){
                            Toast.makeText(Time,"尚未選擇時間",Toast.LENGTH_SHORT).show();
                        }else{
                            dialog.dismiss();
                            showTimePicker();
                        }
                    }
                });

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //dialog show
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(850,1370);
            }
        });

        //set the title
        Title.setText(NameList.get(holder.getAdapterPosition()));

        //set the recycler
        recycler.setLayoutManager(new LinearLayoutManager(Time));
        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        getTheTimeData();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "channelIDRemider";
            String description = "Channel for Alerm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("illessChannelID",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = Time.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showTimePicker(){
        timePickerDialog = new TimePickerDialog(Time, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
                hour = Hour;
                minute = Minute;

                //set the ok button event
                calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY,hour);
                calender.set(Calendar.MINUTE,minute);
                calender.set(Calendar.SECOND,0);
                calender.set(Calendar.MILLISECOND,0);

                String ID = Time.getIntent().getExtras().getString("userID");
                String alermTime = String.format("%02d",hour)+":"+String.format("%02d",minute);
                String title = Title.getText().toString();
                String command = String.format("/userAlermTime/%s/AllTime/%s",ID,alermTime+title+selectTime);

                Map timeMap = new HashMap();
                timeMap.put("time",alermTime);
                timeMap.put("interval",selectTime);
                timeMap.put("name",title);

                dbDR = FirebaseFirestore.getInstance().document(command);
                dbDR.set(timeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getTheTimeData();
                    }
                });

                //set alarm
                alarmManager = (AlarmManager) Time.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(Time,alermReceicer.class);
                pendingIntent = PendingIntent.getBroadcast(Time,0,intent,0);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                Toast.makeText(Time,"提醒設定完成",Toast.LENGTH_SHORT).show();
            }
        },12,00,true);

        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setTitle("請設定提醒時間");
        //show the dialog
        timePickerDialog.show();
    }

    private void getTheTimeData(){
        TimeList.clear();
        SelectTimeList.clear();

        String ID = Time.getIntent().getExtras().getString("userID");
        String command = String.format("/userAlermTime/%s/AllTime",ID);

        dbCR = FirebaseFirestore.getInstance().collection(command);
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getId().contains(Title.getText().toString())){
                        TimeList.add(documentSnapshot.get("time").toString());
                        SelectTimeList.add(documentSnapshot.get("interval").toString());
                    }
                }
                adapter = new timeDetailAdapter(TimeList,SelectTimeList,Time);
                recycler.setAdapter(adapter);
            }
        });
    }

    private View.OnClickListener buttonClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.buttonMorning1:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group45);
                    selectTime = "moring";
                    break;
                case R.id.buttonNoon1:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group44);
                    selectTime = "noon";
                    break;
                case R.id.buttonNight1:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group43);
                    selectTime = "night";
                    break;
                case R.id.buttonBed1:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group42);
                    selectTime = "bed";
                    break;
            }
        }
    };

    private void resetButtonStyle1(){
        moring.setBackgroundResource(R.drawable.group36);
        noon.setBackgroundResource(R.drawable.group37);
        night.setBackgroundResource(R.drawable.group38);
        bed.setBackgroundResource(R.drawable.group39);
    }
}
