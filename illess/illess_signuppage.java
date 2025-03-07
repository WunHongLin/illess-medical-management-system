package com.example.illess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class illess_signuppage extends AppCompatActivity {

    private EditText ID,Password,Name,Sexual,Age,Height,Mail,mailAuthEditText;
    private TextView ButtonSignup,mailAuthButton,mailCloseButton;
    private Button ButtonTurnBack;
    private DocumentReference dbDR;
    private Dialog dialog;
    private View viewDialog;
    private int lock;
    private String email,subject,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_signuppage);
        //start
        ButtonSignup = (TextView) findViewById(R.id.ButtonSignup);
        ButtonTurnBack = (Button) findViewById(R.id.healthDetailButtonTurnBack);
        //set the signup event
        ButtonSignup.setOnClickListener(customerSign);
        //set the close event
        ButtonTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //close this page
            }
        });
    }

    private View.OnClickListener customerSign = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ID = (EditText) findViewById(R.id.signupID);
            Password = (EditText) findViewById(R.id.signupPassword);
            Name = (EditText) findViewById(R.id.signupName);
            Sexual = (EditText) findViewById(R.id.signupSexual);
            Age = (EditText) findViewById(R.id.signupAge);
            Height = (EditText) findViewById(R.id.signupHeight);
            Mail = (EditText) findViewById(R.id.signupMail);
            //init the lock number
            lock = 0;
            //check the id is not empty
            if(ID.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"ID wrong",Toast.LENGTH_LONG).show();
                ID.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                ID.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                ID.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                ID.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                ID.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                ID.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            //check the password is not empty
            if(Password.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"Password wrong",Toast.LENGTH_LONG).show();  // change the style of the edittext
                Password.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                Password.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                Password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                Password.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                Password.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                Password.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            //check the password is not empty
            if(Name.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"Name wrong",Toast.LENGTH_LONG).show();
                Name.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                Name.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                Name.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                Name.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                Name.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                Name.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            //check the password is not empty
            if(Sexual.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"Sexual wrong",Toast.LENGTH_LONG).show();
                Sexual.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                Sexual.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                Sexual.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                Sexual.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                Sexual.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                Sexual.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            //check the password is not empty
            if(Age.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"Age wrong",Toast.LENGTH_LONG).show();
                Age.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                Age.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                Age.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                Age.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                Age.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                Age.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            //check the password is not empty
            if(Height.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"Height wrong",Toast.LENGTH_LONG).show();
                Height.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                Height.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                Height.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                Height.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                Height.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                Height.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            //check the password is not empty
            if(Mail.getText().toString().isEmpty()){
                Toast.makeText(illess_signuppage.this,"Mail wrong",Toast.LENGTH_LONG).show();
                Mail.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.pink));
                Mail.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.red));
                Mail.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_error_24,0);
                lock+=1;
            }else{
                Mail.setBackgroundTintList(ContextCompat.getColorStateList(illess_signuppage.this,R.color.light_blue));
                Mail.setHintTextColor(ContextCompat.getColor(illess_signuppage.this,R.color.blue));
                Mail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }

            // start insert the data,if lock>0 means error otherwise is ok
            if(lock==0){
                sendTheMail();
                createDialog();
            }
        }
    };

    private void createDialog(){
        //create the dialog of the auth
        dialog = new Dialog(illess_signuppage.this);
        viewDialog = getLayoutInflater().inflate(R.layout.pop_up3,null);
        dialog.setContentView(viewDialog);

        //get the views
        mailAuthEditText = (EditText) viewDialog.findViewById(R.id.mailDialogAuth);
        mailAuthButton = (TextView) viewDialog.findViewById(R.id.mailDialogAuthButton);
        mailCloseButton = (TextView) viewDialog.findViewById(R.id.mailDialogCloseButton);

        //show the dialog
        dialog.show();
        dialog.getWindow().setLayout(900,1050);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set the event
        //set the check button event
        mailAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check is or not empty
                if(!(mailAuthEditText.getText().toString().isEmpty())){
                    //check is equal to auth
                    if(mailAuthEditText.getText().toString().equals(content)){
                        dbDR = FirebaseFirestore.getInstance().document("userInformation/"+ID.getText().toString());
                        dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    // change the style of id
                                    Toast.makeText(illess_signuppage.this,"has the same ID",Toast.LENGTH_LONG).show();
                                }else{
                                    User userInfo = new User(ID.getText().toString(),Password.getText().toString(),Name.getText().toString(),Sexual.getText().toString(),Long.parseLong(Age.getText().toString()),Long.parseLong(Height.getText().toString()),Mail.getText().toString(),Long.valueOf(0));
                                    dbDR = FirebaseFirestore.getInstance().document("userInformation/"+userInfo.getID());
                                    dbDR.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.v("123","Success");
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        Toast.makeText(illess_signuppage.this,"驗證碼錯誤，重新輸入",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(illess_signuppage.this,"驗證欄位不得為空",Toast.LENGTH_LONG).show();
                }
            }
        });

        //set the close event
        mailCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void sendTheMail(){
        email = Mail.getText().toString();
        subject = "illess 註冊驗證碼";
        content = Integer.toString((int) (Math.random()*100000+1));

        javaMailAPI mailAPI = new javaMailAPI(illess_signuppage.this,email,subject,content);

        mailAPI.execute();
    }
}