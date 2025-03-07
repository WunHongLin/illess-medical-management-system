package com.example.illess;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.protocol.HTTP;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class illess_shop extends AppCompatActivity {

    private RecyclerView shopRecycler1,shoppingCarRecycler;
    private ArrayList<String> shopRecyclerTitle = new ArrayList<String>();
    private ArrayList<ShoppingCar> shoppingCarList = new ArrayList<ShoppingCar>();
    private ArrayList<product> shopProductList = new ArrayList<product>();
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private shopDetailAdapter detailAdapter;
    private shopRecyclerAdapter shopAdapter;
    private shopRecyclerAdapter2 shopAdapter2;
    private Button shopBtnTurnBack,shopBtnGoCar;
    private Button shoppingCarTurnBack;
    private TextView shoppingCarGoCash,Coin;
    private TextView shoppingCarTotalCash,discountOK,discountCancel;
    private EditText shopProdctSearch,discount;
    private Dialog dialog;
    private View viewDialog;
    private String amount,name,gmail;

    @Override
    public void onDestroy() {
        stopService(new Intent(illess_shop.this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_shop);

        init();
    }

    public void init(){
        //set the shop recycler
        shopRecycler1 = (RecyclerView) findViewById(R.id.shopRecycler);
        shopRecycler1.setOverScrollMode(View.OVER_SCROLL_NEVER);
        shopRecycler1.setLayoutManager(new LinearLayoutManager(illess_shop.this));

        shopRecyclerTitle.clear();

        //download the category from the database
        dbCR = FirebaseFirestore.getInstance().collection("/productCategory");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document:task.getResult()){ shopRecyclerTitle.add(document.getId()); }
                shopAdapter = new shopRecyclerAdapter(shopRecyclerTitle,illess_shop.this);
                shopRecycler1.setAdapter(shopAdapter);
            }
        });

        setButtonTurnBack(); //call the setButtonTurnBack event
        setButtonGoCar();  //call the setButtonGoCar event

        //get the edittext
        shopProdctSearch = (EditText) findViewById(R.id.shopSearch);

        //set the search edittext text change event
        shopProdctSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //check if is empty
                if(shopProdctSearch.getText().toString().isEmpty()){
                    //reset the recycler
                    shopRecycler1 = (RecyclerView) findViewById(R.id.shopRecycler);
                    shopRecycler1.setLayoutManager(new LinearLayoutManager(illess_shop.this));

                    shopRecyclerTitle.clear();

                    //download the category from the database
                    dbCR = FirebaseFirestore.getInstance().collection("/productCategory");
                    dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot document:task.getResult()){ shopRecyclerTitle.add(document.getId()); }
                            shopAdapter = new shopRecyclerAdapter(shopRecyclerTitle,illess_shop.this);
                            shopRecycler1.setAdapter(shopAdapter);
                        }
                    });

                }else{
                    //reset the product recycler
                    shopRecycler1 = (RecyclerView) findViewById(R.id.shopRecycler);
                    shopRecycler1.setLayoutManager(new GridLayoutManager(illess_shop.this,2));

                    shopProductList.clear();

                    //download all product
                    dbCR = FirebaseFirestore.getInstance().collection("/productInformation");
                    dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot:task.getResult()){
                                String str1 = documentSnapshot.getId();
                                String str2 = shopProdctSearch.getText().toString();
                                Boolean isContained = str1.toLowerCase().contains(str2.toLowerCase());
                                if(isContained){
                                    shopProductList.add(documentSnapshot.toObject(product.class));
                                }
                            }
                            detailAdapter = new shopDetailAdapter(shopProductList,illess_shop.this);
                            shopRecycler1.setAdapter(detailAdapter);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //here set the turnback event
    private void setButtonTurnBack(){
        //init the button
        shopBtnTurnBack = (Button) findViewById(R.id.shopButtonTurnBack);
        shopBtnTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //set the Button to gO TO shoppingCar
    private void setButtonGoCar(){
        shopBtnGoCar = (Button) findViewById(R.id.shopButtonGoCar);
        shopBtnGoCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.shopping_cart);
                //here set the event of the shoppingCar view event
                //here init the event of the turnback and go cash event
                setShoppingCarBtnTurnBack();
                setShoppingCarGoCash();
                setShoppingCarRecycler();

                Coin = (TextView) findViewById(R.id.shopOfCoin);
                dbDR = FirebaseFirestore.getInstance().document("/userInformation/"+getIntent().getExtras().getString("userID"));
                dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Coin.setText(String.valueOf(documentSnapshot.get("money")));
                    }
                });
            }
        });
    }

    //set the Button TurnBack
    public void setShoppingCarBtnTurnBack(){
        shoppingCarTurnBack = (Button) findViewById(R.id.shoppingCarTurnBack);
        shoppingCarTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set the view to shop
                setContentView(R.layout.activity_illess_shop);
                //here call the init(),to set all event
                init();
            }
        });
    }

    //set the cash event
    public void setShoppingCarGoCash(){
        shoppingCarGoCash = (TextView) findViewById(R.id.shoppingCarGoCash);
        shoppingCarTotalCash = (TextView) findViewById(R.id.shoppingCarTotalPrice);

        shoppingCarGoCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check is use discount
                if(shoppingCarList.size() > 0){
                    createDialog();
                }else{
                    Toast.makeText(illess_shop.this,"目前沒有商品喔",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //here set the recycler of the recycler
    public void setShoppingCarRecycler(){
        //init the recycler
        shoppingCarRecycler = (RecyclerView) findViewById(R.id.shoppingCarRecycler);
        shoppingCarRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        shoppingCarRecycler.setLayoutManager(new LinearLayoutManager(illess_shop.this));

        //clear the ArrayList
        shoppingCarList.clear();

        //download the data from database
        dbCR = FirebaseFirestore.getInstance().collection("/userShoppingCar/"+getIntent().getExtras().getString("userID")+"/totalProduct");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document:task.getResult()){ shoppingCarList.add(document.toObject(ShoppingCar.class)); }
                shoppingCarAdapter adapter = new shoppingCarAdapter(shoppingCarList, illess_shop.this);
                shoppingCarRecycler.setAdapter(adapter);
            }
        });
    }
    private void createDialog(){
        dialog = new Dialog(illess_shop.this);
        viewDialog = getLayoutInflater().inflate(R.layout.pop_up8,null);
        dialog.setContentView(viewDialog);

        discount = (EditText) viewDialog.findViewById(R.id.discountEditText);
        discountOK = (TextView) viewDialog.findViewById(R.id.discountDialogAddButtton);
        discountCancel = (TextView) viewDialog.findViewById(R.id.discountDialogCancel);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        discountOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(discount.getText().toString().isEmpty())){
                    if(Integer.valueOf(discount.getText().toString()) > Integer.valueOf(Coin.getText().toString())){
                        Toast.makeText(illess_shop.this,"餘額不足，請重新輸入",Toast.LENGTH_SHORT).show();
                    }else{
                        int Discount = Integer.valueOf(discount.getText().toString()) / 10;
                        amount = shoppingCarTotalCash.getText().toString();
                        int AfterDiscount = Integer.valueOf(amount) - Discount;
                        shoppingCarTotalCash.setText(String.valueOf(AfterDiscount));
                        amount = shoppingCarTotalCash.getText().toString();
                        String userID = getIntent().getExtras().getString("userID");

                        //get the email and name from database
                        dbDR = FirebaseFirestore.getInstance().document("/userInformation/"+userID);
                        dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                gmail = task.getResult().get("mail").toString();
                                name = task.getResult().get("name").toString();

                                DocumentSnapshot documentSnapshot = task.getResult();
                                Long number = (Long) documentSnapshot.get("money");
                                dbDR.update("money", number - Long.valueOf(discount.getText().toString()));

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://10.51.204.167/api.php?amount="+amount+"&gmail="+gmail));
                                startActivity(intent);

                                dialog.dismiss();
                                setContentView(R.layout.activity_illess_shop);
                                //here call the init(),to set all event
                                init();
                            }
                        });

                        dbCR = FirebaseFirestore.getInstance().collection("/userShoppingCar/"+getIntent().getExtras().getString("userID")+"/totalProduct");
                        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot documentSnapshot: task.getResult()){
                                    dbDR =FirebaseFirestore.getInstance().document("/userShoppingCar/"+getIntent().getExtras().getString("userID")+"/totalProduct/"+documentSnapshot.getId());
                                    dbDR.delete();
                                }
                            }
                        });
                    }
                }
            }
        });

        discountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //show the dialog
        dialog.show();
        dialog.getWindow().setLayout(900,1050);
    }
}

