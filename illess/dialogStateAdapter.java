package com.example.illess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class dialogStateAdapter extends RecyclerView.Adapter<dialogStateAdapter.ViewHolder>{
    private ArrayList<String> List;
    private illess_frontpage Context;

    public dialogStateAdapter(ArrayList<String> list, illess_frontpage context) {
        List = list;
        Context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item15,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.illNumber.setText(String.format("%02d",position+1));
        holder.illName.setText(List.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context.createRecordDialog(holder.illName.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView illNumber,illName;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            illNumber = (TextView) itemView.findViewById(R.id.recyclerIllNumber);
            illName = (TextView) itemView.findViewById(R.id.recyclerIllName);
            view = itemView;
        }
    }
}
