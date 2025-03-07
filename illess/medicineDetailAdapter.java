package com.example.illess;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class medicineDetailAdapter extends RecyclerView.Adapter<medicineDetailAdapter.ViewHolder>{
    private ArrayList<MedicineInfo> arrayList;
    private ArrayList<String> arrayTempList = new ArrayList<String>();
    private illess_medicine Medicine;
    private StorageReference storageReferance,pickReferance;
    private String SelectTime;
    private DocumentReference dbDR;
    private CollectionReference dbCR;

    public medicineDetailAdapter(ArrayList<MedicineInfo> arrayList, illess_medicine medicine,String selectTime) {
        this.arrayList = arrayList;
        this.Medicine = medicine;
        this.SelectTime = selectTime;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item11,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(arrayList.get(position).getName());
        holder.txtNumber.setText(String.valueOf(arrayList.get(position).getNumber())+"粒");

        //here start to connect to firebase
        storageReferance = FirebaseStorage.getInstance().getReference();
        pickReferance = storageReferance.child(arrayList.get(position).getImageUri());

        try {
            final File file1 = File.createTempFile("images",".jpg");
            pickReferance.getFile(file1).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    holder.image.setImageURI(Uri.fromFile(file1));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.txtName.getCurrentTextColor() == Color.parseColor("#FFFFFF")){
                    holder.txtName.setTextColor(Color.parseColor("#3B98FF"));
                    holder.txtNumber.setTextColor(Color.parseColor("#3B98FF"));
                    holder.view.setBackgroundResource(R.drawable.rectangle4);

                    //remove the data from datalist
                    arrayTempList.remove(holder.txtName.getText().toString());
                }else{
                    holder.txtName.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.txtNumber.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.view.setBackgroundResource(R.drawable.rectangle7);

                    //add the current data into datalist
                    arrayTempList.add(holder.txtName.getText().toString());
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Medicine.getIntent().getExtras().getString("userID");
                String illName = ((TextView) Medicine.findViewById(R.id.medicineComparedName)).getText().toString();
                String MedicineName = holder.txtName.getText().toString();
                String command = String.format("/userMedicineManage/%s/allIll/%s/%s/%s",id,illName,SelectTime,MedicineName);
                dbDR = FirebaseFirestore.getInstance().document(command);
                dbDR.delete();
                arrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private ImageView image;
        private Button delete;
        private TextView txtName,txtNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.medicineImage);
            txtName = (TextView) itemView.findViewById(R.id.medicineName);
            txtNumber = (TextView) itemView.findViewById(R.id.medicineNumber);
            delete = (Button) itemView.findViewById(R.id.MedicineDeleteButton);
            view = itemView;
        }
    }

    public ArrayList<String> getList(){
        return arrayTempList;
    }
}
