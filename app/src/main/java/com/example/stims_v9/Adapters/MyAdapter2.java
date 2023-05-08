package com.example.stims_v9.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stims_v9.Model.Model2;
import com.example.stims_v9.R;

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    ArrayList<Model2> mList2;
    Context context;

    public MyAdapter2(Context context, ArrayList<Model2> mList2){

        this.mList2 = mList2;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item2, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model2 model2 = mList2.get(position);
        holder.student_name.setText(model2.getStudentName());

    }

    @Override
    public int getItemCount() {
        return mList2.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView student_name;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            student_name = itemView.findViewById(R.id.text_view_student_name);
        }
    }
}