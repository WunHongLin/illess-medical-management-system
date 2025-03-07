package com.example.illess;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
import java.util.Map;

public class healthAdapter extends RecyclerView.Adapter<healthAdapter.viewHolder> {
    private ArrayList<String> illName;
    private TextView healthIllName,dialogAddIllValue,dialogAddValueTitle,dialogButtonAddValue,dialogButtonAddClose,healthAddValue,healthWatchMore,healthDetailTitle;
    private Button chartTurnBack,healthDetailTurnBack,healthDetailSearchButton;
    private ArrayList<String> yearList,monthList;
    private illess_health Health;
    private Spinner detailMonth,detailYear;
    private LineChart healthIllChart;
    private DocumentReference dbDR;
    private CollectionReference dbCR;
    private RecyclerView healthDetailRecycler;
    private ArrayList<healthInfoValue> chartData = new ArrayList<healthInfoValue>();
    private ArrayList<HashMap<String,String>> healthDetailMap = new ArrayList<HashMap<String,String>>();
    private healthDetailAdapter detailAdapter;
    private String userID;
    private Dialog dialog;
    private View viewDialog;

    public healthAdapter(ArrayList<String> illname,TextView healthillName,LineChart healthillChart,String userid,illess_health health) {
        illName = illname;
        healthIllName = healthillName;
        healthIllChart = healthillChart;
        userID = userid;
        Health = health;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item9,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.txtTitle.setText(illName.get(position));

        switch(position%3){
            case 0:
                holder.image.setImageResource(R.drawable.group26);
                holder.txtTitle.setBackground(ContextCompat.getDrawable(holder.image.getContext(), R.drawable.btn_bg24));
                break;
            case 1:
                holder.image.setImageResource(R.drawable.group27);
                holder.txtTitle.setBackground(ContextCompat.getDrawable(holder.image.getContext(),R.drawable.btn_bg28));
                break;
            case 2:
                holder.image.setImageResource(R.drawable.group28);
                holder.txtTitle.setBackground(ContextCompat.getDrawable(holder.image.getContext(),R.drawable.btn_bg29));
                break;
            default:
                //do-nothing
        }

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here add the click event
                //first init the layout below the recycler
                Health.setContentView(R.layout.health_chart);

                healthIllName = (TextView) Health.findViewById(R.id.healthIllName);
                healthIllName.setText(holder.txtTitle.getText().toString());

                initThisState();
            }
        });
    }

    @Override
    public int getItemCount() {
        return illName.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle;
        private ImageView image;
        private View myView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.healthCategoryTitle);
            image = (ImageView) itemView.findViewById(R.id.healthCategoryImage);
            myView = itemView;
        }
    }

    private void setChart(){
        //init the chart
        Calendar calendar = Calendar.getInstance();
        chartData.clear();
        healthIllChart.invalidate();
        healthIllChart.clear();

        //select the date from database
        dbCR = FirebaseFirestore.getInstance().collection("/userHealthInfo/"+userID+"/illName/"+healthIllName.getText().toString()+"/DateManager");
        dbCR.whereEqualTo("year",Integer.toString(calendar.get(Calendar.YEAR))).whereEqualTo("month",Integer.toString(calendar.get(Calendar.MONTH)+1)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document:task.getResult()){
                    healthInfoValue userHealthValue = document.toObject(healthInfoValue.class);
                    chartData.add(userHealthValue);
                }
                //set the detail data of the chart
                Log.v("123",Integer.toString(chartData.size()));
                showChart();
            }
        });
    }

    private void showChart(){
        ArrayList<Entry> Line = new ArrayList<Entry>();

        for(int index=0;index<chartData.size();index++){
            Float value = new Float((chartData.get(index).getValue()).floatValue());
            Float v_index = (float) index;
            Line.add(new Entry(v_index,value));
        }

        //Line_end.add(new Entry(Float.parseFloat(Integer.toString(Chartdata.size())),Float.parseFloat(Chartdata.get(Chartdata.size()-1).getValue())));

        LineDataSet set = new LineDataSet(Line,"");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setColor(Color.RED);
        set.setLineWidth(1.5f);//線寬
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setLabel("健康數值");

        LineData LineData = new LineData(set);
        healthIllChart.setData(LineData);
        healthIllChart.invalidate();

        initX();
        initY();
    }

    private void initX(){
        XAxis xAxis = healthIllChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);//X軸標籤顏色
        xAxis.setTextSize(10);
        xAxis.setLabelCount(chartData.size()+1);
        xAxis.setSpaceMin(0.5f);//折線起點距離左側Y軸距離
        xAxis.setSpaceMax(0.5f);//折線終點距離右側Y軸距離

        ArrayList<String> xAxisList = new ArrayList<String>();

        for (int index=0;index<chartData.size();index++){ xAxisList.add(chartData.get(index).getDay()); }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisList));
    }

    private void initY(){
        YAxis rightAxis = healthIllChart.getAxisRight();//獲取右側的軸線
        rightAxis.setEnabled(false);//不顯示右側Y軸
        YAxis leftAxis = healthIllChart.getAxisLeft();//獲取左側的軸線
    }

    private void initThisState(){
        //get the button
        chartTurnBack = (Button) Health.findViewById(R.id.healthChartButtonTurnBack);
        //set event
        chartTurnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Health.setContentView(R.layout.activity_illess_health);
                Health.initTheState();
            }
        });

        //get the view
        healthAddValue = (TextView) Health.findViewById(R.id.healthAddValue);

        //set the event
        healthAddValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add the dialog
                dialog = new Dialog(Health);
                viewDialog = Health.getLayoutInflater().inflate(R.layout.pop_up1,null);
                dialog.setContentView(viewDialog);

                //add the button event
                dialogAddValueTitle = viewDialog.findViewById(R.id.dialogAddValueTitle);
                dialogButtonAddClose = viewDialog.findViewById(R.id.dialogAddValueClose);
                dialogButtonAddValue = viewDialog.findViewById(R.id.dialogAddIllValue);
                dialogAddIllValue = viewDialog.findViewById(R.id.dialogAddEditText);

                //dialog show
                dialogAddValueTitle.setText(healthIllName.getText().toString());
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(900,1050);

                //set the valueAdd event
                dialogButtonAddValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dialogAddIllValue.getText().toString().isEmpty()){
                            Toast.makeText(Health,"欄位尚未填寫",Toast.LENGTH_LONG).show();
                        }else{
                            //init the element to database
                            String userID = Health.getIntent().getExtras().getString("userID");
                            Date Date = new Date(System.currentTimeMillis());
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            String CurrentDate = format.format(Date);

                            //set the date and value information
                            Calendar calendar = Calendar.getInstance();
                            String Year = Integer.toString(calendar.get(Calendar.YEAR));
                            String Month = Integer.toString(calendar.get(Calendar.MONTH)+1);
                            String Day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                            Long Value = Long.parseLong(dialogAddIllValue.getText().toString());
                            healthInfoValue HealthValue = new healthInfoValue(Year,Month,Day,Value);

                            //start to upload the Date
                            dbDR = FirebaseFirestore.getInstance().document("/userHealthInfo/"+userID+"/illName/"+dialogAddValueTitle.getText().toString()+"/DateManager/"+CurrentDate);
                            dbDR.set(HealthValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //setChart
                                    setChart();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });

                //set the dialogClose Event
                dialogButtonAddClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        //get the chart
        healthIllChart = (LineChart) Health.findViewById(R.id.healthIllChart);

        //call the event
        setChart();

        //get the watchMore button
        healthWatchMore = (TextView) Health.findViewById(R.id.healthWatchMore);

        //set the content
        healthWatchMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set the layout
                Health.setContentView(R.layout.health_detail);
                healthDetailTitle = (TextView) Health.findViewById(R.id.healthDetailTitle);
                healthDetailTurnBack = (Button) Health.findViewById(R.id.healthDetailButtonTurnBack);
                detailMonth = (Spinner) Health.findViewById(R.id.detailSpinnerMonth);
                detailYear = (Spinner) Health.findViewById(R.id.detailSpinnerYear);
                healthDetailTitle.setText(healthIllName.getText().toString());

                //set the spinner content
                //init the userInfo
                String userID = Health.getIntent().getExtras().getString("userID");
                Calendar calendar = Calendar.getInstance();

                yearList = new ArrayList<String>();
                monthList = new ArrayList<String>();

                dbCR = FirebaseFirestore.getInstance().collection("/userHealthInfo/"+userID+"/illName/"+healthDetailTitle.getText().toString()+"/DateManager");
                dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot document: task.getResult()){
                            //check if year exist
                            if(!(yearList.contains(document.get("year").toString()))){
                                yearList.add(document.get("year").toString());
                            }
                            //check if month exist
                            if(!(monthList.contains(document.get("month").toString()))){
                                monthList.add(document.get("month").toString());
                            }
                        }

                        //transfer the arraylist to array
                        String[] arrayYear = yearList.toArray(new String[yearList.size()]);
                        String[] arrayMonth = monthList.toArray(new String[monthList.size()]);

                        //set the spinner
                        ArrayAdapter<String> monthAD = new ArrayAdapter<String>(Health,R.layout.support_simple_spinner_dropdown_item,arrayMonth);
                        monthAD.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        detailMonth.setAdapter(monthAD);

                        ArrayAdapter<String> yearAD = new ArrayAdapter<String>(Health,R.layout.support_simple_spinner_dropdown_item,arrayYear);
                        yearAD.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        detailYear.setAdapter(yearAD);
                    }
                });

                //start to set the recycler
                healthDetailRecycler = (RecyclerView) Health.findViewById(R.id.healthDetailRecycler);
                healthDetailRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
                healthDetailRecycler.setLayoutManager(new LinearLayoutManager(Health));

                //download the information
                dbCR = FirebaseFirestore.getInstance().collection("/userHealthInfo/"+userID+"/illName/"+healthDetailTitle.getText().toString()+"/DateManager");
                dbCR.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot document:task.getResult()){
                            //add the data into map
                            HashMap<String,String> Map = new HashMap<String,String>();
                            Map.put("Date", document.getId());
                            Map.put("Value",document.get("value").toString());
                            healthDetailMap.add(Map);
                        }
                        detailAdapter = new healthDetailAdapter(healthDetailMap);
                        healthDetailRecycler.setAdapter(detailAdapter);
                    }
                });

                //set the button event
                healthDetailTurnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        healthDetailMap.clear();
                        detailAdapter.notifyDataSetChanged();
                        Health.setContentView(R.layout.health_chart);
                        initThisState();
                    }
                });

                //init the SearchButton
                healthDetailSearchButton = (Button) Health.findViewById(R.id.DetailFind);

                //here add the Date Search event
                healthDetailSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //get the search limit
                        String searchYear = (String) detailYear.getSelectedItem();
                        String searchMonth = (String) detailMonth.getSelectedItem();

                        //prevent the data reduncy so clear the data
                        healthDetailMap.clear();

                        //down the data
                        dbCR = FirebaseFirestore.getInstance().collection("/userHealthInfo/"+userID+"/illName/"+healthDetailTitle.getText().toString()+"/DateManager");
                        dbCR.whereEqualTo("year",searchYear).whereEqualTo("month",searchMonth).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot document:task.getResult()){
                                    //add the data into map
                                    HashMap<String,String> Map = new HashMap<String,String>();
                                    Map.put("Date", document.getId());
                                    Map.put("Value",document.get("value").toString());
                                    healthDetailMap.add(Map);
                                }

                                //notify the recycler to change
                                detailAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
    }
}
