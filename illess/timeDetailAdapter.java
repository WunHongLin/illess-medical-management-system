package com.example.illess;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class timeDetailAdapter extends RecyclerView.Adapter<timeDetailAdapter.ViewHolder>{

    private ArrayList<String> TimeList;
    private ArrayList<String> SelectTimeList;
    private illess_time Time;
    private DocumentReference dbDR;
    private CollectionReference dbCR;

    public timeDetailAdapter(ArrayList<String> timeList,ArrayList<String> selectTimeList,illess_time time) {
        TimeList = timeList;
        SelectTimeList = selectTimeList;
        Time = time;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item7,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtNumber.setText(String.format("%02d",position+1));
        holder.txtTime.setText(TimeList.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Time.getIntent().getExtras().getString("userID");
                String IllName = ((TextView) Time.findViewById(R.id.timeDeatilTitle)).getText().toString();
                String alermTime = holder.txtTime.getText().toString();
                String selectTime = SelectTimeList.get(holder.getAdapterPosition());
                String command = String.format("/userAlermTime/%s/AllTime/%s",id,alermTime+IllName+selectTime);
                dbDR = FirebaseFirestore.getInstance().document(command);
                dbDR.delete();
                TimeList.remove(holder.getAdapterPosition());
                SelectTimeList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());

                AlarmManager alarmManager = (AlarmManager) Time.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(Time,alermReceicer.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Time,1,intent,0);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(Time,"已經取消該時間提醒",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return TimeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTime,txtNumber;
        private Button delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = (TextView) itemView.findViewById(R.id.ItemTime);
            txtNumber = (TextView) itemView.findViewById(R.id.ItemNumber);
            delete = (Button) itemView.findViewById(R.id.timeDeleteButton);
        }
    }
}
