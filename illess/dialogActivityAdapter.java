package com.example.illess;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class dialogActivityAdapter extends RecyclerView.Adapter<dialogActivityAdapter.ViewHolder>{

    private ArrayList<MedicineInfo> List = new ArrayList<MedicineInfo>();
    private dialogActivity context;
    private StorageReference storageReferance,pickReferance;

    private Button TurnBack,Upload,btnTakePhoto,ToPhoto;
    private ImageView InitImg,ComImage;
    private TextView txtAlermMessage,txtMedicineName;
    private int controll = 0;
    private Uri uri;

    public dialogActivityAdapter(ArrayList<MedicineInfo> list, dialogActivity context) {
        List = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item12,parent,false);
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

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.txtName.getCurrentTextColor() == Color.parseColor("#3B98FF") && controll == 0){
                    holder.txtName.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.txtNumber.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.view.setBackgroundResource(R.drawable.group65);
                    controll = 1;
                }else{
                    holder.txtName.setTextColor(Color.parseColor("#3B98FF"));
                    holder.txtNumber.setTextColor(Color.parseColor("#3B98FF"));
                    holder.view.setBackgroundResource(R.drawable.group75);
                    controll = 0;
                }
                ToPhoto = (Button) context.findViewById(R.id.buttonToPhotoPage1);
                ToPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(controll == 1){
                            context.setContentView(R.layout.medicine_photo);
                            init(holder);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName,txtNumber;
        private ImageView image;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.dialogRecyclerName);
            txtNumber = (TextView) itemView.findViewById(R.id.dialogRecyclerNumber);
            image = (ImageView) itemView.findViewById(R.id.dialogRecyclerImage);
            view = itemView;
        }
    }

    private void init(ViewHolder holder){
        //find the views
        TurnBack = (Button) context.findViewById(R.id.compareTurnBack);
        Upload = (Button) context.findViewById(R.id.buttonUpload);
        InitImg = (ImageView) context.findViewById(R.id.initialImage);
        ComImage = (ImageView) context.findViewById(R.id.comparedImage);
        btnTakePhoto = (Button) context.findViewById(R.id.buttonTakePhoto);
        txtAlermMessage = (TextView) context.findViewById(R.id.alermMessageText);
        txtMedicineName = (TextView) context.findViewById(R.id.medicineComparedName);

        txtMedicineName.setText(List.get(holder.getAdapterPosition()).getName());

        //set the turn back events
        TurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.setContentView(R.layout.medicine_search);
                context.setFindPageContent();
            }
        });

        //set the img
        BitmapDrawable drawable = (BitmapDrawable) holder.image.getDrawable();
        InitImg.setImageBitmap(drawable.getBitmap());

        //set the take photo events
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                context.startActivityForResult(intent,100);
            }
        });

        //set the text alermMessage
        txtAlermMessage.setText("請進行拍照");

        //set the upload event
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReferance = FirebaseStorage.getInstance().getReference();
                pickReferance = storageReferance.child(List.get(holder.getAdapterPosition()).getImageUri());
                pickReferance.delete();

                pickReferance = storageReferance.child(List.get(holder.getAdapterPosition()).getImageUri()); //圖片上船後的名稱
                pickReferance.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context,"上傳成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public Uri getUri(){
        return uri;
    }

    public ImageView getImageView(){
        return ComImage;
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.CAMERA},100);
        }
    }
}
