package com.example.illess;

import android.content.Intent;
import android.util.Log;
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

import java.util.ArrayList;

public class homePageAdapter extends RecyclerView.Adapter<homePageAdapter.ViewHolder>{

    private ArrayList<String> Content;
    private illess_frontpage homePage;
    private Intent intent;

    public homePageAdapter(){}

    public homePageAdapter(ArrayList<String> content,illess_frontpage home) {
        this.Content = content;
        this.homePage = home;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item8,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txt.setText(Content.get(position));

        switch(position % 7){
            case 0:
                holder.image.setImageResource(R.drawable.group90);
                break;
            case 1:
                holder.image.setImageResource(R.drawable.group91);
                break;
            case 2:
                holder.image.setImageResource(R.drawable.group92);
                break;
            case 3:
                holder.image.setImageResource(R.drawable.callcenter);
                break;
            case 4:
                holder.image.setImageResource(R.drawable.group94);
                break;
            case 5:
                holder.image.setImageResource(R.drawable.group95);
                break;
        }
        //set the click event
        holder.homePageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check who is click
                switch (holder.txt.getText().toString()){
                    case "會員資料":
                        intent = new Intent(homePage,illess_member.class);
                        openActivity();
                        break;
                    case "健康資訊":
                        intent = new Intent(homePage,illess_health.class);
                        openActivity();
                        break;
                    case "健康商城":
                        intent = new Intent(homePage,illess_shop.class);
                        openActivity();
                        break;
                    case "智慧助理":
                        homePage.createDialog();
                        break;
                    case "藥物管理":
                        intent = new Intent(homePage,illess_medicine.class);
                        openActivity();
                        break;
                    case "時間提醒":
                        intent = new Intent(homePage,illess_time.class);
                        openActivity();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Content.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txt;
        private ImageView image;
        private View homePageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homePageView = itemView;
            txt = (TextView) itemView.findViewById(R.id.titleOfFunction);
            image = (ImageView) itemView.findViewById(R.id.imageOfFunction);
        }
    }

    private void openActivity(){
        //put the data to the intent
        intent.putExtra("userID",homePage.getIntent().getExtras().getString("userID"));

        //launch the intent
        homePage.openTheActivity.launch(intent);
    }
}
