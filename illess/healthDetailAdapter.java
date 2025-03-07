package com.example.illess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class healthDetailAdapter extends RecyclerView.Adapter<healthDetailAdapter.ViewHolder>{

    private ArrayList<HashMap<String,String>> healthMap;

    public healthDetailAdapter(ArrayList<HashMap<String,String>> healthMap) {
        this.healthMap = healthMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item2Title.setText(healthMap.get(position).get("Date"));
        holder.item2Value.setText(healthMap.get(position).get("Value"));
    }

    @Override
    public int getItemCount() {
        return healthMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView item2Title,item2Value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item2Title = (TextView) itemView.findViewById(R.id.recyclerItem2Title);
            item2Value = (TextView) itemView.findViewById(R.id.recyclerItem2Value);
        }
    }
}
