package com.example.illess;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paypal.android.sdk.payments.PayPalConfiguration;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class shoppingCarAdapter extends RecyclerView.Adapter<shoppingCarAdapter.ViewHolder>{

    private ArrayList<ShoppingCar> CarItem;
    private ArrayList<String> carProductName = new ArrayList<String>();
    private ArrayList<product> carProductInfo = new ArrayList<product>();
    private product productInfo;
    private CollectionReference dbCR;
    private DocumentReference dbDR;
    private StorageReference storageReference,pickReferance;
    private illess_shop shop;
    private TextView totalPrice;
    private int currentProductNum;

    public shoppingCarAdapter(ArrayList<ShoppingCar> carItem, illess_shop shop) {
        this.CarItem = carItem;
        this.shop = shop;

        setTheTotalPrice();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item6,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //init the referance of the storage
        storageReference = FirebaseStorage.getInstance().getReference();
        //get the product from database
        dbCR = FirebaseFirestore.getInstance().collection("/productInformation");
        dbCR.whereEqualTo("name",CarItem.get(position).getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document: task.getResult()){ productInfo = document.toObject(product.class); }
                holder.carItemName.setText(productInfo.getName());
                holder.carItemCategory.setText(productInfo.getCategory());
                holder.carItemPrice.setText(Long.toString(productInfo.getPrice()));

                //get the image
                pickReferance = storageReference.child(productInfo.getUri());

                try {
                    final File file1 = File.createTempFile("images",".png");
                    pickReferance.getFile(file1).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            holder.carItemImage.setImageURI(Uri.fromFile(file1));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //set the num of car product
                holder.carItemCount.setText(String.format("%02d",CarItem.get(holder.getAdapterPosition()).getNum().intValue()));
            }
        });

        holder.carItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbDR = FirebaseFirestore.getInstance().document("/userShoppingCar/"+shop.getIntent().getExtras().getString("userID")+"/totalProduct/"+holder.carItemName.getText().toString());
                dbDR.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CarItem.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        setTheTotalPrice();
                    }
                });
            }
        });

        holder.carItemPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentProductNum = Integer.parseInt(holder.carItemCount.getText().toString());
                currentProductNum += 1;

                holder.carItemCount.setText(String.format("%02d",currentProductNum));

                //update the data in database
                dbDR = FirebaseFirestore.getInstance().document("/userShoppingCar/"+shop.getIntent().getExtras().getString("userID")+"/totalProduct/"+holder.carItemName.getText().toString());

                //count the update total price
                int totalPrice = currentProductNum*Integer.parseInt(holder.carItemPrice.getText().toString());

                dbDR.update("num",Long.valueOf(currentProductNum),"totalPrice",Long.valueOf(totalPrice)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //do - nothing
                        setTheTotalPrice();
                    }
                });
            }
        });

        holder.carItemMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentProductNum = Integer.parseInt(holder.carItemCount.getText().toString());

                if(currentProductNum-1>0){
                    currentProductNum -=1;
                    holder.carItemCount.setText(String.format("%02d",currentProductNum));

                    //update the data in database
                    dbDR = FirebaseFirestore.getInstance().document("/userShoppingCar/"+shop.getIntent().getExtras().getString("userID")+"/totalProduct/"+holder.carItemName.getText().toString());

                    //count the update total price
                    int totalPrice = currentProductNum*Integer.parseInt(holder.carItemPrice.getText().toString());

                    dbDR.update("num",Long.valueOf(currentProductNum),"totalPrice",Long.valueOf(totalPrice)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //do - nothing
                            setTheTotalPrice();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return CarItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView carItemImage;
        private TextView carItemName,carItemCategory,carItemPrice,carItemCount;
        private Button carItemPlus,carItemMinus,carItemDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carItemImage =(ImageView) itemView.findViewById(R.id.carItemImage);

            carItemName = (TextView) itemView.findViewById(R.id.carItemName);
            carItemCategory= (TextView) itemView.findViewById(R.id.carItemCategory);
            carItemPrice = (TextView) itemView.findViewById(R.id.carItemPrice);
            carItemCount = (TextView) itemView.findViewById(R.id.carItemCount);

            carItemMinus = (Button) itemView.findViewById(R.id.carItemMinus);
            carItemPlus = (Button) itemView.findViewById(R.id.carItemPlus);
            carItemDelete = (Button) itemView.findViewById(R.id.carItemDelete);
        }
    }

    private void setTheTotalPrice(){
        dbCR = FirebaseFirestore.getInstance().collection("/userShoppingCar/"+shop.getIntent().getExtras().getString("userID")+"/totalProduct");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int totalPrice1 = 0;
                totalPrice = (TextView) shop.findViewById(R.id.shoppingCarTotalPrice);
                for(DocumentSnapshot document:task.getResult()){
                    totalPrice1 += ((Long)document.get("totalPrice")).intValue();
                }
                totalPrice.setText(Integer.toString(totalPrice1));
            }
        });
    }
}
