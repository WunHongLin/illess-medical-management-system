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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class shopDetailAdapter extends RecyclerView.Adapter<shopDetailAdapter.ViewHolder>{

    private ArrayList<product> Product;
    private illess_shop shop;
    private StorageReference storageReferance,pickReferance;
    private Button btnTurnBack,btnGoCar;
    private TextView txtProductName,txtProductCategory,txtProductPrice,txtProductCount,btnPutCar;
    private ImageView imageProductImage;
    private Button btnPlus,btnMinus;
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private int currentProductNum;

    public shopDetailAdapter(){}

    public shopDetailAdapter(ArrayList<product> productName, illess_shop shop){
        this.Product = productName;
        this.shop = shop;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item5,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set the price and name
        holder.txtProductName.setText(Product.get(position).getName());
        holder.txtProductPrice.setText(Long.toString(Product.get(position).getPrice()));

        //need to set the image
        storageReferance = FirebaseStorage.getInstance().getReference();
        pickReferance = storageReferance.child(Product.get(position).getUri());

        try {
            final File file = File.createTempFile("images",".png");
            pickReferance.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    holder.ProductImage.setImageURI(Uri.fromFile(file));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //here need to add the click event of the item
        holder.productView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set the lauout
                shop.setContentView(R.layout.merchandise_information);

                //start to set the layout event and information
                setProductDetailGoCar();
                setProductDetailTurnBack();

                //set the info and event
                txtProductName = (TextView) shop.findViewById(R.id.productDetailName);
                txtProductCategory = (TextView) shop.findViewById(R.id.productDetailCategory);
                txtProductPrice = (TextView) shop.findViewById(R.id.productDetailPrice);
                txtProductCount = (TextView) shop.findViewById(R.id.productDetailCount);
                btnPutCar = (TextView) shop.findViewById(R.id.productDetailPutCar);
                imageProductImage = (ImageView) shop.findViewById(R.id.productDetailImage);
                btnPlus = (Button) shop.findViewById(R.id.productDetailBtnPlus);
                btnMinus = (Button) shop.findViewById(R.id.productDetailBtnMinus);

                txtProductName.setText(Product.get(holder.getAdapterPosition()).getName());
                txtProductCategory.setText(Product.get(holder.getAdapterPosition()).getCategory());
                txtProductPrice.setText(Long.toString(Product.get(holder.getAdapterPosition()).getPrice()));

                //set image
                pickReferance = storageReferance.child(Product.get(holder.getAdapterPosition()).getUri());

                try {
                    final File file1 = File.createTempFile("images",".png");
                    pickReferance.getFile(file1).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            imageProductImage.setImageURI(Uri.fromFile(file1));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //set the event of put product to the car
                btnPutCar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //count the total num
                        int totalPrice = currentProductNum*Integer.parseInt(txtProductPrice.getText().toString());

                        //create the new class
                        ShoppingCar shoppingCar = new ShoppingCar(txtProductName.getText().toString(),txtProductCategory.getText().toString(),Long.valueOf(currentProductNum),Long.valueOf(totalPrice));

                        dbDR = FirebaseFirestore.getInstance().document("/userShoppingCar/"+shop.getIntent().getExtras().getString("userID")+"/totalProduct/"+txtProductName.getText().toString());
                        dbDR.set(shoppingCar).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                shop.setContentView(R.layout.activity_illess_shop);
                                shop.init();
                            }
                        });
                    }
                });

                //init the current product Num
                currentProductNum = 1;

                //set the event of button plus push
                btnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentProductNum +=1;
                        txtProductCount.setText(String.format("%02d",currentProductNum));
                    }
                });

                //set the event of button minus push
                btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentProductNum-1>0){
                            currentProductNum -=1;
                            txtProductCount.setText(String.format("%02d",currentProductNum));
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return Product.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtProductName,txtProductPrice;
        private ImageView ProductImage;
        private View productView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = (TextView) itemView.findViewById(R.id.shopProductName2);
            txtProductPrice = (TextView) itemView.findViewById(R.id.shopProductPrice2);
            ProductImage = (ImageView) itemView.findViewById(R.id.shopProductImage2);
            productView = itemView;
        }
    }

    //set the turnback event
    private void setProductDetailTurnBack(){
        btnTurnBack = (Button) shop.findViewById(R.id.productDetailTurnBack);
        btnTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shop.setContentView(R.layout.activity_illess_shop);
                shop.init();
            }
        });
    }

    private void setProductDetailGoCar(){
        btnGoCar = (Button) shop.findViewById(R.id.carItemDelete);
        btnGoCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shop.setContentView(R.layout.shopping_cart);
                //set the event of the shoppingCar
                shop.setShoppingCarBtnTurnBack();
                shop.setShoppingCarGoCash();
                shop.setShoppingCarRecycler();
            }
        });
    }
}
