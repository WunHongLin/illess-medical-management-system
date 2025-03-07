package com.example.illess;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class illess_frontpage extends AppCompatActivity {

    private TextView homePageName,assistDialogAlert;
    private ImageView assistDialogImage;
    private Button speechAssistButton,assistDialogButton,assistDialogCancel;
    private RecyclerView homePageRecycler,sideEffectRecycler,illRecycler;
    private SpeechRecognizer mSpeechRecongizer;
    private TextToSpeech speechManager;
    private Intent mRecongizerIntent;
    private Dialog dialog,dialogMenu,dialogSideEffect,dialogRecord,dialogIll,dialogMedicine;
    private View viewDialog,viewDialogMenu,viewDialogSideEffect,viewDialogRecord,viewDialogIll,viewDialogMedicine;
    private ArrayList<String> homeFunction = new ArrayList<String>();
    private ArrayList<String> illNameList = new ArrayList<String>();
    private ArrayList<Map<String,String>> MedicineList = new ArrayList<Map<String,String>>();
    private DocumentReference dbDR,dbDR1,dbDR2,dbDR3,dbDR4;
    private CollectionReference dbCR;
    private String tempString,DateToday;
    private sideEffectAdapter sideAdapter;
    private dialogIllAdapter illAdapter;
    private EditText command;

    private Button Search,State,sideEffect,takeMedicine;
    private TextView modeOK;
    private String CurrentMode="";
    private String[] array;

    private Button morning1,noon1,night1,bed1;
    private RecyclerView MedicineRecycler;
    private String time;
    private String selectName;
    private ArrayList<MedicineInfo> NameList = new ArrayList<MedicineInfo>();
    private dialogMedicineAdapter MedicineAdapter;

    private ArrayList<String> illNameList2 = new ArrayList<String>();
    private dialogStateAdapter stateAdapter;

    private ArrayList<String> illNameList3 = new ArrayList<String>();
    private dialogTakeMedicineAdapter takeAdapter;

    private Dialog dialogInterval;
    private View viewDialogInterval;
    private Button morning2,noon2,night2,bed2;
    private String selectedInterval,selectedIllName;


    private ImageView morning,noon,night,bed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illess_frontpage);

        speechManager = new TextToSpeech(illess_frontpage.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    speechManager.setLanguage(Locale.CHINESE);
                    speechManager.setSpeechRate(1.0f);
                }
            }
        });

        //set the view and recycler
        init();

        checkPermission();
    }

    private void init(){
        //get the textview
        homePageName = (TextView) findViewById(R.id.homePageName);
        //set the text of the textView
        homePageName.setText(getIntent().getExtras().getString("userName"));

        //set the Recycler
        homePageRecycler = (RecyclerView) findViewById(R.id.homePageRecycler);
        homePageRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        homePageRecycler.setLayoutManager(new GridLayoutManager(illess_frontpage.this,2));

        //clear the arraylist
        homeFunction.clear();

        //add the data into list
        homeFunction.add("會員資料");
        homeFunction.add("健康資訊");
        homeFunction.add("健康商城");
        homeFunction.add("智慧助理");
        homeFunction.add("藥物管理");
        homeFunction.add("時間提醒");

        //set the adapter
        homePageAdapter adapter = new homePageAdapter(homeFunction,illess_frontpage.this);
        homePageRecycler.addItemDecoration(new itemDecoration());
        homePageRecycler.setAdapter(adapter);

        //set the recongizer intent
        mSpeechRecongizer = SpeechRecognizer.createSpeechRecognizer(illess_frontpage.this);
        mRecongizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecongizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String language = "zh_TW";
        mRecongizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,language);
        mRecongizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,language);
        mRecongizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,language);

        //set the recongizer
        mSpeechRecongizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> stringList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(stringList != null){
                    commandExecute(stringList);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(illess_frontpage.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent setting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(setting);
                finish();
            }
        }
    }

    public ActivityResultLauncher<Intent> openTheActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //do-nothing
        }
    });

    @SuppressLint("ClickableViewAccessibility")
    public void createDialog(){
        CurrentMode = "";
        //show the dialog
        dialogMenu = new Dialog(illess_frontpage.this);
        viewDialogMenu = getLayoutInflater().inflate(R.layout.pop_up11,null);
        dialogMenu.setContentView(viewDialogMenu);

        Search = (Button) viewDialogMenu.findViewById(R.id.buttonSearch);
        State = (Button) viewDialogMenu.findViewById(R.id.buttonState);
        sideEffect = (Button) viewDialogMenu.findViewById(R.id.buttonSideEffect);
        takeMedicine = (Button) viewDialogMenu.findViewById(R.id.buttonTakeMedicine);
        speechAssistButton = (Button) viewDialogMenu.findViewById(R.id.mainPageSpeechAssist);
        command = (EditText) viewDialogMenu.findViewById(R.id.editTextCommand);

        modeOK = (TextView) viewDialogMenu.findViewById(R.id.textViewOk);

        Search.setOnClickListener(clickEvent);
        State.setOnClickListener(clickEvent);
        sideEffect.setOnClickListener(clickEvent);
        takeMedicine.setOnClickListener(clickEvent);

        modeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentMode.equals("")){
                    //check the editText is not empty
                    if(command.getText().toString().isEmpty()){
                        speechManager.speak("目前沒有輸入內容喔",TextToSpeech.QUEUE_ADD,null);
                    }else{
                        ArrayList<String> List = new ArrayList<String>();
                        List.add(command.getText().toString());
                        commandExecute(List);
                    }
                }else{
                    dialogMenu.dismiss();

                    if(CurrentMode.equals("副作用")){
                        createAssiteDialog();
                        speechManager.speak("已經選擇"+CurrentMode+"，請說出您的副作用",TextToSpeech.QUEUE_ADD,null);
                    }else if(CurrentMode.equals("藥物查詢")){
                        createIllDialog();
                        speechManager.speak("已經選擇"+CurrentMode+"，請選擇您要查詢的病",TextToSpeech.QUEUE_ADD,null);
                    }else if(CurrentMode.equals("用藥狀況")){
                        createIllDialog2();
                        speechManager.speak("已經選擇"+CurrentMode+"，請選擇您要查詢的病",TextToSpeech.QUEUE_ADD,null);
                    }else if(CurrentMode.equals("前往吃藥")){
                        createIllDialog3();
                        speechManager.speak("已經選擇"+CurrentMode+"，請選擇要進行服藥的病名",TextToSpeech.QUEUE_ADD,null);
                    }
                }
            }
        });

        command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetStyle();
                CurrentMode = "";
            }
        });

        //set the event
        speechAssistButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View view) {
                dialogMenu.dismiss();
                speechManager.speak("請說出您的需求",TextToSpeech.QUEUE_ADD,null);
                //add the dialog
                createAssiteDialog();
            }
        });

        dialogMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMenu.getWindow().setLayout(840,1420);
        dialogMenu.show();
    }

    private void createSideEffectDialog(String command){
        MedicineList.clear();

        dialogSideEffect = new Dialog(illess_frontpage.this);
        viewDialogSideEffect = getLayoutInflater().inflate(R.layout.pop_up12,null);
        dialogSideEffect.setContentView(viewDialogSideEffect);

        sideEffectRecycler = (RecyclerView) viewDialogSideEffect.findViewById(R.id.recyclerOfMedicine);
        sideEffectRecycler.setLayoutManager(new LinearLayoutManager(illess_frontpage.this));

        array = command.split("有");

        String commandLine = String.format("/userSideEffect/%s/allSideMedicine",getIntent().getExtras().getString("userID"));
        dbCR = FirebaseFirestore.getInstance().collection(commandLine);
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.get("sideEffect").toString().contains(array[array.length - 1])){
                        Map<String,String> map = new HashMap<String,String>();
                        map.put("name", documentSnapshot.getId());
                        map.put("uri",documentSnapshot.get("imageUri").toString());
                        MedicineList.add(map);
                    }
                }

                sideAdapter = new sideEffectAdapter(MedicineList);
                sideEffectRecycler.setAdapter(sideAdapter);
                dialogSideEffect.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSideEffect.getWindow().setLayout(840,1440);
                dialogSideEffect.show();

                speechManager.speak("以下是可能引起"+array[array.length - 1]+"的藥物",TextToSpeech.QUEUE_ADD,null);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createAssiteDialog(){
        dialog = new Dialog(illess_frontpage.this);
        viewDialog = getLayoutInflater().inflate(R.layout.pop_up4,null);
        dialog.setContentView(viewDialog);

        //find the view in dialog
        assistDialogAlert = (TextView) viewDialog.findViewById(R.id.assistDialogAlert);
        assistDialogButton = (Button) viewDialog.findViewById(R.id.assistDialogButton);
        assistDialogCancel = (Button) viewDialog.findViewById(R.id.assistDialogCancel);
        assistDialogImage = (ImageView) viewDialog.findViewById(R.id.assistDialogImage);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //dialog show
        dialog.show();
        dialog.getWindow().setLayout(840,1440);

        //set the assistDialogButton event
        assistDialogButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        //stop listining
                        mSpeechRecongizer.stopListening();
                        assistDialogImage.setImageResource(R.drawable.headphone);
                        assistDialogAlert.setText("請按下方圖示");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        //start listining
                        mSpeechRecongizer.startListening(mRecongizerIntent);
                        assistDialogImage.setImageResource(R.drawable.customersupport);
                        assistDialogAlert.setText("聆聽需求中");
                        break;
                }
                return false;
            }
        });

        //set the assistDialogCancel
        assistDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogMenu.show();
                resetStyle();
            }
        });
    }

    public void createRecordDialog(String command){
        dialogIll.dismiss();
        speechManager.speak("以下是今天"+command+"的用藥紀錄",TextToSpeech.QUEUE_ADD,null);

        dialogRecord = new Dialog(illess_frontpage.this);
        viewDialogRecord = getLayoutInflater().inflate(R.layout.pop_up13,null);
        dialogRecord.setContentView(viewDialogRecord);

        morning = (ImageView) viewDialogRecord.findViewById(R.id.imageViewMorning);
        noon = (ImageView) viewDialogRecord.findViewById(R.id.imageViewNoon);
        night = (ImageView) viewDialogRecord.findViewById(R.id.imageViewNight);
        bed = (ImageView) viewDialogRecord.findViewById(R.id.imageViewBed);


        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calender.getTime();
        DateToday = simpleDate.format(date);

        String commandRecord1 = String.format("/userMedicineRecord/%s/totalRecord/%s",getIntent().getExtras().getString("userID"),DateToday+command+"moring");
        dbDR = FirebaseFirestore.getInstance().document(commandRecord1);
        dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    morning.setImageResource(R.drawable.group103);
                }
                String commandRecord2 = String.format("/userMedicineRecord/%s/totalRecord/%s",getIntent().getExtras().getString("userID"),DateToday+command+"noon");
                dbDR1 = FirebaseFirestore.getInstance().document(commandRecord2);
                dbDR1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            noon.setImageResource(R.drawable.group103);
                        }
                        String commandRecord3 = String.format("/userMedicineRecord/%s/totalRecord/%s",getIntent().getExtras().getString("userID"),DateToday+command+"night");
                        dbDR2 = FirebaseFirestore.getInstance().document(commandRecord3);
                        dbDR2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    night.setImageResource(R.drawable.group103);
                                }
                                String commandRecord4 = String.format("/userMedicineRecord/%s/totalRecord/%s",getIntent().getExtras().getString("userID"),DateToday+command+"bed");
                                dbDR3 = FirebaseFirestore.getInstance().document(commandRecord4);
                                dbDR3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()){
                                            bed.setImageResource(R.drawable.group103);
                                        }
                                        dialogRecord.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialogRecord.getWindow().setLayout(945,1630);
                                        dialogRecord.show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void createIllDialog(){
        dialogIll = new Dialog(illess_frontpage.this);
        viewDialogIll = viewDialogRecord = getLayoutInflater().inflate(R.layout.pop_up14,null);
        dialogIll.setContentView(viewDialogIll);

        illRecycler = (RecyclerView) viewDialogIll.findViewById(R.id.recycler);
        illRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        illRecycler.setLayoutManager(new LinearLayoutManager(illess_frontpage.this));

        illNameList.clear();

        dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+getIntent().getExtras().getString("userID")+"/allIll");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot:task.getResult()){ illNameList.add(documentSnapshot.getId()); }
                illAdapter = new dialogIllAdapter(illNameList,illess_frontpage.this,viewDialogIll);
                illRecycler.setAdapter(illAdapter);

                dialogIll.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogIll.getWindow().setLayout(945,1650);
                dialogIll.show();
            }
        });
    }

    private void createIllDialog2(){
        dialogIll = new Dialog(illess_frontpage.this);
        viewDialogIll = viewDialogRecord = getLayoutInflater().inflate(R.layout.pop_up14,null);
        dialogIll.setContentView(viewDialogIll);

        illRecycler = (RecyclerView) viewDialogIll.findViewById(R.id.recycler);
        illRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        illRecycler.setLayoutManager(new LinearLayoutManager(illess_frontpage.this));

        illNameList2.clear();

        dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+getIntent().getExtras().getString("userID")+"/allIll");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot:task.getResult()){ illNameList2.add(documentSnapshot.getId()); }
                stateAdapter = new dialogStateAdapter(illNameList2,illess_frontpage.this);
                illRecycler.setAdapter(stateAdapter);

                dialogIll.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogIll.getWindow().setLayout(945,1650);
                dialogIll.show();
            }
        });
    }

    private void createIllDialog3(){
        dialogIll = new Dialog(illess_frontpage.this);
        viewDialogIll = viewDialogRecord = getLayoutInflater().inflate(R.layout.pop_up14,null);
        dialogIll.setContentView(viewDialogIll);

        TextView txt = viewDialogIll.findViewById(R.id.textViewOfRemaining);
        txt.setText("請選擇要進行服藥的病名");

        illRecycler = (RecyclerView) viewDialogIll.findViewById(R.id.recycler);
        illRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        illRecycler.setLayoutManager(new LinearLayoutManager(illess_frontpage.this));

        illNameList3.clear();

        dbCR = FirebaseFirestore.getInstance().collection("/userMedicineManage/"+getIntent().getExtras().getString("userID")+"/allIll");
        dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot:task.getResult()){ illNameList3.add(documentSnapshot.getId()); }
                takeAdapter = new dialogTakeMedicineAdapter(illNameList3,illess_frontpage.this);
                illRecycler.setAdapter(takeAdapter);

                dialogIll.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogIll.getWindow().setLayout(945,1650);
                dialogIll.show();
            }
        });
    }

    public void createMedidineDialog(String Name){
        dialogIll.dismiss();

        dialogMedicine = new Dialog(illess_frontpage.this);
        viewDialogMedicine = getLayoutInflater().inflate(R.layout.pop_up15,null);
        dialogMedicine.setContentView(viewDialogMedicine);

        morning1 = (Button) viewDialogMedicine.findViewById(R.id.buttonMorning1);
        noon1 = (Button) viewDialogMedicine.findViewById(R.id.buttonNoon1);
        night1 = (Button) viewDialogMedicine.findViewById(R.id.buttonNight1);
        bed1 = (Button) viewDialogMedicine.findViewById(R.id.buttonBed1);

        selectName = Name;
        speechManager.speak("已經選擇"+Name,TextToSpeech.QUEUE_ADD,null);
        MedicineRecycler = (RecyclerView) viewDialogMedicine.findViewById(R.id.recycler);

        morning1.setOnClickListener(buttonClick);
        noon1.setOnClickListener(buttonClick);
        night1.setOnClickListener(buttonClick);
        bed1.setOnClickListener(buttonClick);

        speechManager.speak("請選擇您要的時段",TextToSpeech.QUEUE_ADD,null);

        dialogMedicine.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMedicine.getWindow().setLayout(945,1650);
        dialogMedicine.show();
    }

    public void createIntervalDialog(String illName){
        speechManager.speak("已經選擇"+illName+" 請選擇現在的時段",TextToSpeech.QUEUE_ADD,null);
        dialogIll.dismiss();
        selectedIllName = illName;

        dialogInterval = new Dialog(illess_frontpage.this);
        viewDialogInterval = getLayoutInflater().inflate(R.layout.pop_up16,null);
        dialogInterval.setContentView(viewDialogInterval);

        morning2 = viewDialogInterval.findViewById(R.id.buttonMorning1);
        noon2 = viewDialogInterval.findViewById(R.id.buttonNoon1);
        night2 = viewDialogInterval.findViewById(R.id.buttonNight1);
        bed2 = viewDialogInterval.findViewById(R.id.buttonBed1);

        morning2.setOnClickListener(selectedEvent);
        noon2.setOnClickListener(selectedEvent);
        night2.setOnClickListener(selectedEvent);
        bed2.setOnClickListener(selectedEvent);

        dialogInterval.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInterval.getWindow().setLayout(950,920);
        dialogInterval.show();
    }

    private View.OnClickListener clickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonSearch:
                    resetStyle();
                    view.setBackgroundResource(R.drawable.group105);
                    CurrentMode = "藥物查詢";
                    break;
                case R.id.buttonState:
                    resetStyle();
                    view.setBackgroundResource(R.drawable.group107);
                    CurrentMode = "用藥狀況";
                    break;
                case R.id.buttonSideEffect:
                    resetStyle();
                    view.setBackgroundResource(R.drawable.group109);
                    CurrentMode = "副作用";
                    break;
                case R.id.buttonTakeMedicine:
                    resetStyle();
                    view.setBackgroundResource(R.drawable.group112);
                    CurrentMode = "前往吃藥";
                    break;
            }
        }
    };

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonMorning1:
                    time = "moring";
                    resetStyle2();
                    morning1.setBackgroundResource(R.drawable.group45);
                    break;
                case R.id.buttonNoon1:
                    time = "noon";
                    resetStyle2();
                    noon1.setBackgroundResource(R.drawable.group44);
                    break;
                case R.id.buttonNight1:
                    time = "night";
                    resetStyle2();
                    night1.setBackgroundResource(R.drawable.group43);
                    break;
                case R.id.buttonBed1:
                    time = "bed";
                    resetStyle2();
                    bed1.setBackgroundResource(R.drawable.group42);
                    break;
            }

            NameList.clear();

            //set the recycler
            MedicineRecycler.setLayoutManager(new LinearLayoutManager(illess_frontpage.this));
            MedicineRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

            String command = String.format("/userMedicineManage/%s/allIll/%s/%s",getIntent().getExtras().getString("userID"),selectName,time);
            dbCR = FirebaseFirestore.getInstance().collection(command);
            dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot documentSnapshot:task.getResult()){ NameList.add(documentSnapshot.toObject(MedicineInfo.class)); }
                    MedicineAdapter = new dialogMedicineAdapter(NameList);
                    MedicineRecycler.setAdapter(MedicineAdapter);
                }
            });
        }
    };

    private View.OnClickListener selectedEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonMorning1:
                    selectedInterval = "moring";
                    break;
                case R.id.buttonNoon1:
                    selectedInterval = "noon";
                    break;
                case R.id.buttonNight1:
                    selectedInterval = "night";
                    break;
                case R.id.buttonBed1:
                    selectedInterval = "bed";
                    break;
            }

            Calendar mCal = Calendar.getInstance();

            String ID = getIntent().getExtras().getString("userID");
            String currentTime = (String) DateFormat.format("HH:mm", mCal.getTime());
            String command = String.format("/userAlermTime/%s/AllTime/%s",ID,currentTime+selectedIllName+selectedInterval);

            Map timeMap = new HashMap();
            timeMap.put("time",currentTime);
            timeMap.put("interval",selectedInterval);
            timeMap.put("name",selectedIllName);

            dbDR = FirebaseFirestore.getInstance().document(command);
            dbDR.set(timeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialogInterval.dismiss();
                    startActivity(new Intent(illess_frontpage.this,dialogActivity.class));
                }
            });
        }
    };

    private void resetStyle(){
        Search.setBackgroundResource(R.drawable.group106);
        State.setBackgroundResource(R.drawable.group108);
        sideEffect.setBackgroundResource(R.drawable.group110);
        takeMedicine.setBackgroundResource(R.drawable.group111);
    }

    private void resetStyle2() {
        morning1.setBackgroundResource(R.drawable.group36);
        noon1.setBackgroundResource(R.drawable.group37);
        night1.setBackgroundResource(R.drawable.group38);
        bed1.setBackgroundResource(R.drawable.group39);
    }

    private void commandExecute(ArrayList<String> stringList){
        if(CurrentMode.equals("副作用")){
            if(stringList.get(0).toString().contains("有") || stringList.get(0).toString().contains("")){
                //check the calender system
                dialog.dismiss();
                createSideEffectDialog(stringList.get(0));
                CurrentMode = "";
            }
        }else if(CurrentMode.equals("用藥狀況")){
            String command = String.format("/userMedicineManage/%s/allIll/%s",getIntent().getExtras().getString("userID"),stringList.get(0));
            dbDR = FirebaseFirestore.getInstance().document(command);
            dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()){
                        dialog.dismiss();
                        speechManager.speak("已經找到"+stringList.get(0)+"相關結果",TextToSpeech.QUEUE_ADD,null);
                        createRecordDialog(stringList.get(0));
                        CurrentMode = "";
                    }else{
                        speechManager.speak("沒有找到該疾病喔",TextToSpeech.QUEUE_ADD,null);
                        CurrentMode = "";
                    }
                }
            });
        }else if(CurrentMode.equals("藥物查詢")){

        }else{
            if(stringList.get(0).toString().contains("打開") && stringList.get(0).toString().contains("會員")){
                //check the member system
                if(dialog != null){
                    dialog.dismiss();
                }else{
                    dialogMenu.dismiss();
                }
                Intent intent = new Intent(illess_frontpage.this,illess_member.class);
                intent.putExtra("userID",getIntent().getExtras().getString("userID"));
                speechManager.speak("已經打開會員系統",TextToSpeech.QUEUE_ADD,null);
                openTheActivity.launch(intent);
            }else if(stringList.get(0).toString().contains("打開") && stringList.get(0).toString().contains("健康")){
                //check the health system
                if(dialog != null){
                    dialog.dismiss();
                }else{
                    dialogMenu.dismiss();
                }
                Intent intent = new Intent(illess_frontpage.this,illess_health.class);
                intent.putExtra("userID",getIntent().getExtras().getString("userID"));
                speechManager.speak("已經打開健康系統",TextToSpeech.QUEUE_ADD,null);
                openTheActivity.launch(intent);
            }else if(stringList.get(0).toString().contains("打開") && stringList.get(0).toString().contains("商城")){
                //check the shop system
                if(dialog != null){
                    dialog.dismiss();
                }else{
                    dialogMenu.dismiss();
                }
                Intent intent = new Intent(illess_frontpage.this,illess_shop.class);
                intent.putExtra("userID",getIntent().getExtras().getString("userID"));
                speechManager.speak("已經打開商城系統",TextToSpeech.QUEUE_ADD,null);
                openTheActivity.launch(intent);
            }else if(stringList.get(0).toString().contains("打開") && stringList.get(0).toString().contains("藥物管理")){
                //check the calender system
                if(dialog != null){
                    dialog.dismiss();
                }else{
                    dialogMenu.dismiss();
                }
                Intent intent = new Intent(illess_frontpage.this,illess_medicine.class);
                intent.putExtra("userID",getIntent().getExtras().getString("userID"));
                speechManager.speak("已經打開藥物管理系統",TextToSpeech.QUEUE_ADD,null);
                openTheActivity.launch(intent);
            }else if(stringList.get(0).toString().contains("打開") && stringList.get(0).toString().contains("時間提醒")){
                //check the calender system
                if(dialog != null){
                    dialog.dismiss();
                }else{
                    dialogMenu.dismiss();
                }
                Intent intent = new Intent(illess_frontpage.this,illess_time.class);
                intent.putExtra("userID",getIntent().getExtras().getString("userID"));
                speechManager.speak("已經打開時間提醒",TextToSpeech.QUEUE_ADD,null);
                openTheActivity.launch(intent);
            }else if(stringList.get(0).toString().contains("藥物管理") && (stringList.get(0).toString().contains("新增") || stringList.get(0).toString().contains("加入"))){
                //check the calender system
                dialog.dismiss();
                String userID = getIntent().getExtras().getString("userID");

                if(stringList.get(0).toString().contains("新增")){
                    array = stringList.get(0).split("新增");
                }else if(stringList.get(0).toString().contains("加入")){
                    array = stringList.get(0).split("加入");
                }

                dbDR = FirebaseFirestore.getInstance().document("/userMedicineManage/"+userID+"/allIll/"+array[array.length-1]);
                Map<String,Object> emptyMap = new HashMap<String,Object>();
                dbDR.set(emptyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // add success close the dialog
                        Intent intent = new Intent(illess_frontpage.this,illess_medicine.class);
                        intent.putExtra("userID",getIntent().getExtras().getString("userID"));
                        openTheActivity.launch(intent);
                        speechManager.speak("已經在藥物管理系統新增"+array[array.length-1],TextToSpeech.QUEUE_ADD,null);

                        if(dialog != null){
                            dialog.dismiss();
                        }else{
                            dialogMenu.dismiss();
                        }
                    }
                });
            }else if(stringList.get(0).toString().contains("健康系統") && (stringList.get(0).toString().contains("新增") || stringList.get(0).toString().contains("加入"))) {
                //check the calender system
                dialog.dismiss();
                String userID = getIntent().getExtras().getString("userID");

                if (stringList.get(0).toString().contains("新增")) {
                    array = stringList.get(0).split("新增");
                } else if (stringList.get(0).toString().contains("加入")) {
                    array = stringList.get(0).split("加入");
                }

                dbDR = FirebaseFirestore.getInstance().document("/userHealthInfo/" + userID + "/illName/" + array[array.length - 1]);
                Map<String, Object> emptyMap = new HashMap<String, Object>();
                dbDR.set(emptyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // add success close the dialog
                        Intent intent = new Intent(illess_frontpage.this, illess_health.class);
                        intent.putExtra("userID", getIntent().getExtras().getString("userID"));
                        openTheActivity.launch(intent);
                        speechManager.speak("已經在健康系統新增" + array[array.length - 1], TextToSpeech.QUEUE_ADD, null);

                        if(dialog != null){
                            dialog.dismiss();
                        }else{
                            dialogMenu.dismiss();
                        }
                    }
                });
            }else if ((stringList.get(0).toString().contains("將") || stringList.get(0).toString().contains("在")) && (stringList.get(0).toString().contains("購物車")) && (stringList.get(0).toString().contains("新增") || stringList.get(0).toString().contains("加入"))) {
                //check the calender system
                dialog.dismiss();
                String userID = getIntent().getExtras().getString("userID");

                if (stringList.get(0).toString().contains("在")) {
                    tempString = stringList.get(0).split("在")[1];
                } else if (stringList.get(0).toString().contains("將")) {
                    tempString = stringList.get(0).split("將")[1];
                }


                if (tempString.contains("新增")) {
                    array = tempString.split("新增");
                } else if (tempString.contains("加入")) {
                    array = tempString.split("加入");
                }

                dbDR = FirebaseFirestore.getInstance().document("/productInformation/" + array[array.length - 1]);
                dbDR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            product temp = documentSnapshot.toObject(product.class);
                            //create the new class
                            ShoppingCar shoppingCar = new ShoppingCar(temp.getName(), temp.getCategory(), Long.valueOf(1), temp.getPrice());
                            dbDR1 = FirebaseFirestore.getInstance().document("/userShoppingCar/" + userID + "/totalProduct/" + array[array.length - 1]);
                            dbDR1.set(shoppingCar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(illess_frontpage.this, illess_shop.class);
                                    intent.putExtra("userID", getIntent().getExtras().getString("userID"));
                                    openTheActivity.launch(intent);
                                    speechManager.speak("已經在購物車新增" + array[array.length - 1], TextToSpeech.QUEUE_ADD, null);

                                    if(dialog != null){
                                        dialog.dismiss();
                                    }else{
                                        dialogMenu.dismiss();
                                    }
                                }
                            });
                        } else {
                            speechManager.speak("找不到該商品", TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });
            }else if(stringList.get(0).toString().contains("清空") && stringList.get(0).toString().contains("購物車")){
                String userID = getIntent().getExtras().getString("userID");
                dbCR = FirebaseFirestore.getInstance().collection("/userShoppingCar/1084533/totalProduct");
                dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot:task.getResult()){
                            dbDR = FirebaseFirestore.getInstance().document("/userShoppingCar/"+userID+"/totalProduct/"+documentSnapshot.getId());
                            dbDR.delete();
                        }
                        speechManager.speak("已經清空購物車", TextToSpeech.QUEUE_ADD, null);

                        if(dialog != null){
                            dialog.dismiss();
                        }else{
                            dialogMenu.dismiss();
                        }
                    }
                });
            }else{
                //say does not get the command
                speechManager.speak("找不到相符合的指令",TextToSpeech.QUEUE_ADD,null);
            }
        }
    }
}