package com.example.illess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class illess_member extends AppCompatActivity {

    private TextView Name,Age,Height,Mail,dialogTitle,dialogButtonUpdate,dialogButtonClose;
    private Button TurnBack;
    private Dialog dialog;
    private EditText dialogEditText;
    private View viewDialog;
    private DocumentReference dbDR;
    private CollectionReference dbCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_member);

        //set the all views and events
        init();
    }

    private void init(){

        //get the view of button
        TurnBack = (Button) findViewById(R.id.personUpdateTurnBack);
        //set the turnback events
        TurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //set all views
        setAllViews();

        //set the Name click event
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog("姓名","name");
            }
        });


        //set the Age click event
        Age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog("年齡","age");
            }
        });

        //set the Height click event
        Height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog("身高","height");
            }
        });

        //set the Age click event
        Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog("電子郵件","mail");
            }
        });
    }

    private void setDialog(String itemTitle,String itemContent){
        //add the dialog
        dialog = new Dialog(illess_member.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewDialog = getLayoutInflater().inflate(R.layout.pop_up2,null);
        dialog.setContentView(viewDialog);

        //set the dialog views
        dialogTitle = (TextView) viewDialog.findViewById(R.id.PersonDialogTitle);
        dialogEditText = (EditText) viewDialog.findViewById(R.id.PersonDialogNewData);
        dialogButtonUpdate = (TextView) viewDialog.findViewById(R.id.PersonDialogUpdateButton);
        dialogButtonClose = (TextView) viewDialog.findViewById(R.id.PersonDialogCloseButton);

        //dialog show
        dialog.show();
        dialog.getWindow().setLayout(900,1050);

        //set the title
        dialogTitle.setText(itemTitle);

        //set the update event
        dialogButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogEditText.getText().toString().isEmpty()){
                    //change the style of the edittext
                }else{
                    dbDR = FirebaseFirestore.getInstance().document("/userInformation/"+getIntent().getExtras().getString("userID"));

                    //check if is the height or age
                    if(itemContent.equals("name") || itemContent.equals("mail")){
                        dbDR.update(itemContent,dialogEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //update success
                                dialog.dismiss();
                                setAllViews();
                            }
                        });
                    }else{
                        dbDR.update(itemContent,Long.parseLong(dialogEditText.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //update success
                                dialog.dismiss();
                                setAllViews();
                            }
                        });
                    }
                }
            }
        });

        //set the dialog close event
        dialogButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void setAllViews(){
        Name = (TextView) findViewById(R.id.personUpdateName);
        Age = (TextView) findViewById(R.id.ItemTime);
        Height = (TextView) findViewById(R.id.personUpdateHeight);
        Mail = (TextView) findViewById(R.id.personUpdateMail);

        //download the information of the user
        dbCR = FirebaseFirestore.getInstance().collection("/userInformation/");
        dbCR.whereEqualTo("id",getIntent().getExtras().getString("userID")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document: task.getResult()){
                    User user = document.toObject(User.class);
                    Name.setText(user.getName());
                    Age.setText(Long.toString(user.getAge()));
                    Height.setText(Long.toString(user.getHeight()));
                    Mail.setText(user.getMail());
                }
            }
        });
    }
}