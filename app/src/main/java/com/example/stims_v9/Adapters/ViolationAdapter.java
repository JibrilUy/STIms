package com.example.stims_v9.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stims_v9.Model.Model;
import com.example.stims_v9.Model.ViolationModel;
import com.example.stims_v9.R;

import java.util.ArrayList;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.MyViewHolder> {

    ArrayList<ViolationModel> violationList;
    Context context;

    public ViolationAdapter(Context context, ArrayList<ViolationModel>violationList){

        this.violationList = violationList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_violation, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ViolationModel violationModel = violationList.get(position);

        holder.textViewViolationItemViolation.setText(violationModel.getViolation());
        holder.textViewDescriptionItemViolation.setText(violationModel.getDescription());

    }

    @Override
    public int getItemCount() {
        return violationList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewViolationItemViolation, textViewDescriptionItemViolation;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            textViewViolationItemViolation = itemView.findViewById(R.id.textViewViolationItemViolation);
            textViewDescriptionItemViolation = itemView.findViewById(R.id.textViewDescriptionItemViolation);
        }
    }
}
