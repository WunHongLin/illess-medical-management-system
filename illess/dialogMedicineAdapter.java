package com.example.illess;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class dialogMedicineAdapter extends RecyclerView.Adapter<dialogMedicineAdapter.ViewHolder>{

    private ArrayList<MedicineInfo> List;
    private StorageReference storageReferance,pickReferance;

    public dialogMedicineAdapter(ArrayList<MedicineInfo> list) {
        List = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item16,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(List.get(position).getName());
        holder.txtNumber.setText(String.valueOf(List.get(position).getNumber())+"粒");

        //here start to connect to firebase
        storageReferance = FirebaseStorage.getInstance().getReference();
        pickReferance = storageReferance.child(List.get(position).getImageUri());

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
        return List.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView txtName,txtNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.medicineImage);
            txtName = (TextView) itemView.findViewById(R.id.medicineName);
            txtNumber = (TextView) itemView.findViewById(R.id.medicineNumber);
        }
    }
}
