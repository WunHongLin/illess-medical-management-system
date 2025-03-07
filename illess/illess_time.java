package com.example.illess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class illess_time extends AppCompatActivity {

    private Button TurnBack;
    private RecyclerView recycler;
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private timeIllAdapter adapter;
    private ArrayList<String> illNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_time);

        init();
    }

    public void init(){
        //find the views
        TurnBack = (Button) findViewById(R.id.timeTurnBack);
        recycler = (RecyclerView) findViewById(R.id.timeRecycler);

        //set the turn back event
        TurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //set the recycler
        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recycler.setLayoutManager(new LinearLayoutManager(illess_time.this));

        //clear the data
        illNameList.clear();

        //set the data
        dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+getIntent().getExtras().getString("userID")+"/allIll");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){ illNameList.add(documentSnapshot.getId()); }
                adapter = new timeIllAdapter(illNameList,illess_time.this);
                recycler.setAdapter(adapter);
            }
        });
    }
}