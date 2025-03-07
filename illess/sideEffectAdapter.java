package com.example.illess;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class sideEffectAdapter extends RecyclerView.Adapter<sideEffectAdapter.ViewHolder>{

    private ArrayList<Map<String,String>> MedicineList;
    private StorageReference storageReferance,pickReferance;

    public sideEffectAdapter(ArrayList<Map<String, String>> medicineList) {
        MedicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item14,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt.setText(MedicineList.get(position).get("name"));
        //here start to connect to firebase
        storageReferance = FirebaseStorage.getInstance().getReference();
        pickReferance = storageReferance.child(MedicineList.get(position).get("uri"));

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
    }

    @Override
    public int getItemCount() {
        return MedicineList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView txt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageMedicine);
            txt = (TextView) itemView.findViewById(R.id.textViewMedicineName);
        }
    }
}
