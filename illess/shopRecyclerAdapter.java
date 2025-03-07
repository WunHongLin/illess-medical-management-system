package com.example.illess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class shopRecyclerAdapter extends RecyclerView.Adapter<shopRecyclerAdapter.ViewHolder>{

    private ArrayList<String> itemTitle;
    private illess_shop shop;
    private ArrayList<product> itemProductName;
    private ArrayList<product> shopDetailProducName = new ArrayList<product>();
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private shopRecyclerAdapter2 shopAdapter2;
    private Button shopDetailTurnBack,shopDetailGoCar;
    private TextView shopDetailTitle,shopDetailDes;
    private RecyclerView shopViewMoreRecycler;
    private EditText shopDetailSearch;

    public shopRecyclerAdapter(){}

    public shopRecyclerAdapter(ArrayList<String> itemTitle,illess_shop shop) {
        this.itemTitle = itemTitle;
        this.shop = shop;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item3,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set the category of the product
        holder.txtTitle.setText(itemTitle.get(position));

        //add the more event(jump to the next activity)
        holder.btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shop.setContentView(R.layout.shop_detail);
                //init the textView
                shopDetailTitle = (TextView) shop.findViewById(R.id.shopDetailTitle);
                shopDetailDes = (TextView) shop.findViewById(R.id.shopDetailDes);

                //set the all event of the shopDetail
                setViewMoreGoCar();
                setViewMoreTurnBack();

                //set the viewMore recycler
                //claer the data
                shopDetailProducName.clear();

                //here set the recycler
                shopViewMoreRecycler = (RecyclerView) shop.findViewById(R.id.shopDetailRecycler);
                shopViewMoreRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
                shopViewMoreRecycler.setLayoutManager(new GridLayoutManager(shopViewMoreRecycler.getContext(),2));

                //download the data from database
                dbCR = FirebaseFirestore.getInstance().collection("/productInformation");
                dbCR.whereEqualTo("category",itemTitle.get(holder.getAdapterPosition())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot document: task.getResult()){ shopDetailProducName.add(document.toObject(product.class)); }
                        shopDetailAdapter adapter = new shopDetailAdapter(shopDetailProducName,shop);
                        shopViewMoreRecycler.setAdapter(adapter);

                        //set the title and des of the shopDetail
                        shopDetailDes.setText("搜尋到"+Integer.toString(shopDetailProducName.size())+"筆商品");
                        shopDetailTitle.setText(holder.txtTitle.getText().toString());
                    }
                });

                //find the edittext
                shopDetailSearch = (EditText) shop.findViewById(R.id.shopDetailSearch);

                //set the event
                shopDetailSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(shopDetailSearch.getText().toString().isEmpty()){
                            //reset the data
                            //claer the data
                            shopDetailProducName.clear();

                            //here set the recycler
                            shopViewMoreRecycler = (RecyclerView) shop.findViewById(R.id.shopDetailRecycler);
                            shopViewMoreRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
                            shopViewMoreRecycler.setLayoutManager(new GridLayoutManager(shopViewMoreRecycler.getContext(),2));

                            //download the data from database
                            dbCR = FirebaseFirestore.getInstance().collection("/productInformation");
                            dbCR.whereEqualTo("category",itemTitle.get(holder.getAdapterPosition())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(DocumentSnapshot document: task.getResult()){ shopDetailProducName.add(document.toObject(product.class)); }
                                    shopDetailAdapter adapter = new shopDetailAdapter(shopDetailProducName,shop);
                                    shopViewMoreRecycler.setAdapter(adapter);

                                    //set the title and des of the shopDetail
                                    shopDetailDes.setText("搜尋到"+Integer.toString(shopDetailProducName.size())+"筆商品");
                                    shopDetailTitle.setText(holder.txtTitle.getText().toString());
                                }
                            });
                        }else{
                            //reset the search data
                            //claer the data
                            shopDetailProducName.clear();

                            //here set the recycler
                            shopViewMoreRecycler = (RecyclerView) shop.findViewById(R.id.shopDetailRecycler);
                            shopViewMoreRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
                            shopViewMoreRecycler.setLayoutManager(new GridLayoutManager(shopViewMoreRecycler.getContext(),2));

                            //download the data from database
                            dbCR = FirebaseFirestore.getInstance().collection("/productInformation");
                            dbCR.whereEqualTo("category",itemTitle.get(holder.getAdapterPosition())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(DocumentSnapshot document: task.getResult()){
                                        String str1 = document.getId();
                                        String str2 = shopDetailSearch.getText().toString();
                                        Boolean isContained = str1.toLowerCase().contains(str2.toLowerCase());
                                        if(isContained){
                                            shopDetailProducName.add(document.toObject(product.class));
                                        }
                                    }
                                    shopDetailAdapter adapter = new shopDetailAdapter(shopDetailProducName,shop);
                                    shopViewMoreRecycler.setAdapter(adapter);

                                    //set the title and des of the shopDetail
                                    shopDetailDes.setText("搜尋到"+Integer.toString(shopDetailProducName.size())+"筆商品");
                                }
                            });
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        //set the recycler
        holder.shopRecycler2.setLayoutManager(new LinearLayoutManager(holder.shopRecycler2.getContext(),LinearLayoutManager.HORIZONTAL,false));
        holder.shopRecycler2.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //download the data to arrayList
        //here need to add the limit by the category name
        dbCR = FirebaseFirestore.getInstance().collection("/productInformation");
        dbCR.whereEqualTo("category",itemTitle.get(position)).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemProductName = new ArrayList<product>();

                //here need to get all info of the product
                for(DocumentSnapshot document:task.getResult()){ itemProductName.add(document.toObject(product.class)); }
                shopAdapter2 = new shopRecyclerAdapter2(itemProductName,shop);
                holder.shopRecycler2.setAdapter(shopAdapter2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemTitle.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle,btnViewMore;
        private RecyclerView shopRecycler2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.shopRecyclerItem1Title);
            btnViewMore = (TextView) itemView.findViewById(R.id.shopRecyclerViewMoreButton);
            shopRecycler2 = (RecyclerView) itemView.findViewById(R.id.shopRecycler2);
        }
    }

    //set the turnback event
    private void setViewMoreTurnBack(){
        shopDetailTurnBack = (Button) shop.findViewById(R.id.shopDetailTurnBack);
        shopDetailTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shop.setContentView(R.layout.activity_illess_shop);
                shop.init();
            }
        });
    }

    private void setViewMoreGoCar(){
        shopDetailGoCar = (Button) shop.findViewById(R.id.shopDetailGoCar);
        shopDetailGoCar.setOnClickListener(new View.OnClickListener() {
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

