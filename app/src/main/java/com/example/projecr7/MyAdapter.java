package com.example.projecr7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.database.Dinner;

import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<com.example.projecr7.MyAdapter.ViewHolder> {
    private Dinner[] dinners;
    private onClickInterface mOnClickInterface;

    // RecyclerView recyclerView;
    public MyAdapter(Dinner[] dinners, onClickInterface mOnClickInterface) {
        this.dinners = dinners;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public com.example.projecr7.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.dinner_list_item, parent, false);
        com.example.projecr7.MyAdapter.ViewHolder viewHolder = new com.example.projecr7.MyAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.example.projecr7.MyAdapter.ViewHolder holder, final int position) {
        holder.textView_name.setText(dinners[position].getDinnerName());
        holder.textView_date.setText(dinners[position].getDinnerDate() + "/" + dinners[position].getDinnerMonth() + "/" + dinners[position].getDinnerYear());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(dinners[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dinners.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_name;
        public TextView textView_date;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView_name = itemView.findViewById(R.id.textView_dinner_name);
            this.textView_date = itemView.findViewById(R.id.textView_dinner_time);
            linearLayout = itemView.findViewById(R.id.LinerLayout_dinner_item);
        }
    }

}

