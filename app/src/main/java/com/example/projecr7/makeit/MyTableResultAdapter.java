package com.example.projecr7.makeit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.R;

public class MyTableResultAdapter extends RecyclerView.Adapter<MyTableResultAdapter.ViewHolder>{
    private String[] nameArray;

    public MyTableResultAdapter(String[] nameArray) {
        this.nameArray = nameArray;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.single_table_result_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewName.setText(nameArray[position]);
        holder.textViewSeatNumber.setText("Seat. " + (position+1));
    }


    @Override
    public int getItemCount() {
        return nameArray.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewSeatNumber;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.result_item_name_textview);
            this.textViewSeatNumber = itemView.findViewById(R.id.result_item_seatId_textview);
        }
    }
}