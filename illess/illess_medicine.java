package com.example.illess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class illess_medicine extends AppCompatActivity {

    private Button medicineTurnBack,medicineAddIll;
    private TextView dialogAddIll,dialogCancel;
    private EditText dialogIllName,medicineIllSearch;
    private RecyclerView illRecycler;
    private ArrayList<String> illNameList = new ArrayList<String>();
    private medicineAdapter adapter;
    private Uri uri;
    private String dataList;
    private Dialog dialog;
    private View viewDialog;
    private int active = 1;
    private Bitmap currentImage;
    private CollectionReference dbCR;
    private DocumentReference dbDR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_medicine);

        init();
    }

    public void init(){
        //find the views
        medicineTurnBack = (Button) findViewById(R.id.medicineTurnBack);
        medicineAddIll = (Button) findViewById(R.id.midicineAddIll);
        medicineIllSearch = (EditText) findViewById(R.id.medicineIllSearch);
        illRecycler = (RecyclerView) findViewById(R.id.medicineRecycler);

        //set the turnback event
        medicineTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //clear the arrayList
        illNameList.clear();

        //set the recycler
        illRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        illRecycler.setLayoutManager(new LinearLayoutManager(illess_medicine.this));

        //find the data from database
        String userID = getIntent().getExtras().getString("userID");
        dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+userID+"/allIll");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot:task.getResult()){
                    illNameList.add(documentSnapshot.getId());
                }
                adapter = new medicineAdapter(illNameList,illess_medicine.this);
                illRecycler.setAdapter(adapter);
            }
        });

        //set the button add ill event
        medicineAddIll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call dialog function
                showDialog();
            }
        });

        //set the edittext search event
        medicineIllSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //clear the arrayList
                illNameList.clear();

                if(medicineIllSearch.getText().toString().isEmpty()){
                    //find the data from database
                    String userID = getIntent().getExtras().getString("userID");
                    dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+userID+"/allIll");
                    dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot:task.getResult()){
                                illNameList.add(documentSnapshot.getId());
                            }
                            adapter = new medicineAdapter(illNameList,illess_medicine.this);
                            illRecycler.setAdapter(adapter);
                        }
                    });
                }else{
                    //find the data from database
                    String userID = getIntent().getExtras().getString("userID");
                    dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+userID+"/allIll");
                    dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot:task.getResult()){
                                String str1 = documentSnapshot.getId();
                                String str2 = medicineIllSearch.getText().toString();
                                Boolean result = str1.toLowerCase().contains(str2.toLowerCase());
                                if(result){
                                    illNameList.add(documentSnapshot.getId());
                                }
                            }
                            adapter = new medicineAdapter(illNameList,illess_medicine.this);
                            illRecycler.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }

    private void showDialog(){
        //set the add ill dialog event
        dialog = new Dialog(illess_medicine.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewDialog = getLayoutInflater().inflate(R.layout.pop_up6,null);
        dialog.setContentView(viewDialog);

        //find the views
        dialogAddIll = (TextView) viewDialog.findViewById(R.id.medicineDialogAddButtton);
        dialogCancel = (TextView) viewDialog.findViewById(R.id.medicineDialogCancel);
        dialogIllName = (EditText) viewDialog.findViewById(R.id.medicineIllEditText);

        //set the event
        dialogAddIll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dialogIllName.getText().toString().isEmpty())){
                    String userID = getIntent().getExtras().getString("userID");
                    dbDR = FirebaseFirestore.getInstance().document("/userMedicineManage/"+userID+"/allIll/"+dialogIllName.getText().toString());
                    Map<String,Object> emptyMap = new HashMap<String,Object>();
                    dbDR.set(emptyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // add success close the dialog
                            dialog.dismiss();
                            illNameList.add(dialogIllName.getText().toString());
                            adapter.notifyDataSetChanged();
                        }
                    });
                }else{
                    Toast.makeText(illess_medicine.this,"尚未填寫欄位，請重新輸入",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(900,1050);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            currentImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(adapter.getUri()),null,null);
        } catch (FileNotFoundException e) {

        }
        //set the image of the view in medicineAdapter
        adapter.getImageView().setImageBitmap(currentImage);
    }
}