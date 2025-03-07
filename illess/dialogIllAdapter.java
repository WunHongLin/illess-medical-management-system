package com.example.illess;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class dialogIllAdapter extends RecyclerView.Adapter<dialogIllAdapter.ViewHolder>{
    private ArrayList<String> List;
    private illess_frontpage HomeContext;
    private View ViewDialog;
    private RecyclerView recycler;
    private ArrayList<MedicineInfo> MedicineList = new ArrayList<MedicineInfo>();


    public dialogIllAdapter(ArrayList<String> list, illess_frontpage homeContext,View viewDialog) {
        List = list;
        HomeContext = homeContext;
        ViewDialog = viewDialog;
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
                HomeContext.createMedidineDialog(holder.illName.getText().toString());
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
