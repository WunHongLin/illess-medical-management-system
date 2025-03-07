package com.example.illess;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class medicineAdapter extends RecyclerView.Adapter<medicineAdapter.ViewHolder>{
    private ArrayList<String> illName;
    private illess_medicine Medicine;
    private TextView txtTitle;
    private Button TurnBack,moring,noon,night,bed,addMedicine,copy,paste,photo,confirm,deny;
    private EditText editTextMedicineName,editTextMedicineNumber,editTextMedicineSideEffect;
    public ImageView imageOfMedicine;
    private Dialog dialog;
    private View viewDialog;
    private String selectTime,copyTime;
    private medicineDetailAdapter adapter;
    private Uri uri;
    private RecyclerView recycler;
    private ArrayList<MedicineInfo> arrayList = new ArrayList<MedicineInfo>();
    private ArrayList<String> copyList;
    private StorageReference storageReferance,pickReferance;
    private DocumentReference dbDR,dbDR2,dbDR3;
    private CollectionReference dbCR;

    public medicineAdapter(ArrayList<String> illName, illess_medicine medicine) {
        this.illName = illName;
        this.Medicine = medicine;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item10,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.illNumber.setText(String.format("%02d",position+1));
        holder.illName.setText(illName.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Medicine.setContentView(R.layout.medicine_detail);
                init(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return illName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView illNumber,illName;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            illNumber = (TextView) itemView.findViewById(R.id.recyclerIllNumber);
            illName = (TextView) itemView.findViewById(R.id.recyclerIllName);
            view = itemView;
        }
    }

    private void init(ViewHolder holder){
        //find the views
        txtTitle = (TextView) Medicine.findViewById(R.id.medicineComparedName);
        TurnBack = (Button) Medicine.findViewById(R.id.compareTurnBack);
        moring = (Button) Medicine.findViewById(R.id.buttonMorning);
        noon = (Button) Medicine.findViewById(R.id.buttonNoon);
        night = (Button) Medicine.findViewById(R.id.buttonNight);
        bed = (Button) Medicine.findViewById(R.id.buttonBed);
        addMedicine = (Button) Medicine.findViewById(R.id.buttonAdd);
        copy = (Button) Medicine.findViewById(R.id.buttonCopy);
        paste = (Button) Medicine.findViewById(R.id.buttonPaste);
        recycler = (RecyclerView) Medicine.findViewById(R.id.medicineDetailRecycler);

        //init the views
        txtTitle.setText(illName.get(holder.getAdapterPosition()));

        //set click events
        TurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Medicine.setContentView(R.layout.activity_illess_medicine);
                Medicine.init();
            }
        });

        //set the event
        moring.setOnClickListener(buttonClickEvent);
        noon.setOnClickListener(buttonClickEvent);
        night.setOnClickListener(buttonClickEvent);
        bed.setOnClickListener(buttonClickEvent);

        //set the event
        addMedicine.setOnClickListener(FunctionalButtonClick);
        copy.setOnClickListener(FunctionalButtonClick);
        paste.setOnClickListener(FunctionalButtonClick);
    }

    private View.OnClickListener buttonClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.buttonMorning:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group45);
                    selectTime = "moring";
                    break;
                case R.id.buttonNoon:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group44);
                    selectTime = "noon";
                    break;
                case R.id.buttonNight:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group43);
                    selectTime = "night";
                    break;
                case R.id.buttonBed:
                    resetButtonStyle1();
                    view.setBackgroundResource(R.drawable.group42);
                    selectTime = "bed";
                    break;
            }

            setRecyclerContent();
        }
    };

    //set the add copy paste button style event
    private View.OnClickListener FunctionalButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(selectTime != null){
                switch(view.getId()){
                    case R.id.buttonAdd:
                        resetButtonStyle2();
                        view.setBackgroundResource(R.drawable.group47);
                        createDialog();
                        break;
                    case R.id.buttonCopy:
                        resetButtonStyle2();
                        view.setBackgroundResource(R.drawable.group49);
                        copyEvent();
                        break;
                    case R.id.buttonPaste:
                        resetButtonStyle2();
                        view.setBackgroundResource(R.drawable.group51);
                        pasteEvent();
                        break;
                }
            }else{
                Toast.makeText(Medicine,"尚未選擇時間",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void resetButtonStyle1(){
        moring.setBackgroundResource(R.drawable.group36);
        noon.setBackgroundResource(R.drawable.group37);
        night.setBackgroundResource(R.drawable.group38);
        bed.setBackgroundResource(R.drawable.group39);
    }

    private void resetButtonStyle2(){
        addMedicine.setBackgroundResource(R.drawable.group46);
        copy.setBackgroundResource(R.drawable.group48);
        paste.setBackgroundResource(R.drawable.group50);
    }

    //call the add medicine dialog
    private void createDialog(){
        dialog = new Dialog(Medicine);
        viewDialog = Medicine.getLayoutInflater().inflate(R.layout.pop_up7,null);
        dialog.setContentView(viewDialog);

        //find the views
        photo = (Button) viewDialog.findViewById(R.id.buttonPhoto);
        confirm = (Button) viewDialog.findViewById(R.id.buttonConfirm);
        deny = (Button) viewDialog.findViewById(R.id.buttonDeny);
        editTextMedicineName = (EditText) viewDialog.findViewById(R.id.editTextMedicineName);
        editTextMedicineNumber = (EditText) viewDialog.findViewById(R.id.editTextMedicineNumber);
        editTextMedicineSideEffect = (EditText) viewDialog.findViewById(R.id.editTextMedicineSideEffect);
        imageOfMedicine = (ImageView) viewDialog.findViewById(R.id.imageOfMedicine);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set the event
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(editTextMedicineName.getText().toString().isEmpty()) || !(editTextMedicineNumber.getText().toString().isEmpty()) || !(editTextMedicineSideEffect.getText().toString().isEmpty())){
                    //create the new object
                    String Name = editTextMedicineName.getText().toString();
                    Long Number = Long.parseLong(editTextMedicineNumber.getText().toString());
                    String imageName = Medicine.getIntent().getExtras().getString("userID") + selectTime + Name + ".jpg";
                    String sideEffect = editTextMedicineSideEffect.getText().toString();
                    MedicineInfo medicine = new MedicineInfo(Name,Number,imageName,sideEffect);

                    //upload the data to the database
                    String uploadPath = String.format("/userMedicineManage/%s/allIll/%s/%s/%s",Medicine.getIntent().getExtras().get("userID"),txtTitle.getText().toString(),selectTime,editTextMedicineName.getText().toString());
                    dbDR = FirebaseFirestore.getInstance().document(uploadPath);
                    dbDR.set(medicine).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dbDR3 = FirebaseFirestore.getInstance().document("/userSideEffect/"+Medicine.getIntent().getExtras().get("userID")+"/allSideMedicine/"+editTextMedicineName.getText().toString());
                            dbDR3.set(medicine).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    /*do-nothing*/
                                }
                            });
                        }
                    });

                    //here start to connect to firebase
                    storageReferance = FirebaseStorage.getInstance().getReference();

                    pickReferance = storageReferance.child(imageName); //圖片上船後的名稱
                    pickReferance.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

                    //close the dialog
                    dialog.dismiss();

                    //set my recycler
                    setRecyclerContent();
                }else{
                    Toast.makeText(Medicine,"有欄位尚未輸入",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //set the event deny
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //set the photo event
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                uri = Medicine.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                Medicine.startActivityForResult(intent,100);
            }
        });

        //show the dialog
        dialog.show();
        dialog.getWindow().setLayout(813,1622);
    }

    private void copyEvent(){
        copyTime = selectTime;
        copyList = adapter.getList();
    }

    private void pasteEvent(){
        for(int index=0;index<copyList.size();index++){
            String command1 = String.format("/userMedicineManage/%s/allIll/%s/%s/%s",Medicine.getIntent().getExtras().get("userID"),txtTitle.getText().toString(),copyTime,copyList.get(index));
            dbDR = FirebaseFirestore.getInstance().document(command1);
            dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    MedicineInfo info = document.toObject(MedicineInfo.class);

                    String command2 = String.format("/userMedicineManage/%s/allIll/%s/%s/%s",Medicine.getIntent().getExtras().get("userID"),txtTitle.getText().toString(),selectTime,info.getName());
                    dbDR2 = FirebaseFirestore.getInstance().document(command2);
                    dbDR2.set(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            setRecyclerContent();
                        }
                    });
                }
            });
        }
    }

    private void setRecyclerContent(){
        //set the recycler content
        arrayList.clear();
        //set recycler
        recycler.setLayoutManager(new LinearLayoutManager(Medicine));
        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        String userID = Medicine.getIntent().getExtras().getString("userID");
        String command = String.format("/userMedicineManage/%s/allIll/%s/%s",userID,txtTitle.getText().toString(),selectTime);
        dbCR = FirebaseFirestore.getInstance().collection(command);
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document:task.getResult()){
                    arrayList.add(document.toObject(MedicineInfo.class));
                }
                adapter = new medicineDetailAdapter(arrayList,Medicine,selectTime);
                recycler.setAdapter(adapter);
            }
        });
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(Medicine, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Medicine,new String[]{Manifest.permission.CAMERA},100);
        }
    }

    public ImageView getImageView(){
        return imageOfMedicine;
    };

    public Uri getUri(){
        return uri;
    }


}
