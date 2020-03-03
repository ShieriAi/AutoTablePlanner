package com.example.projecr7.bribes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.R;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.onClickInterface;

public class MyBribeListAdapter extends RecyclerView.Adapter<MyBribeListAdapter.ViewHolder>{
    private Bribe[] bribedata;
    private onClickInterface mOnClickInterface;

    public MyBribeListAdapter(Bribe[] bribedata, onClickInterface mOnClickInterface) {
        this.bribedata = bribedata;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.bribe_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewName.setText(bribedata[position].getGuestName());
        holder.textViewAmount.setText("£ " + bribedata[position].getBribeAmount());
        holder.textViewSatis.setText(Integer.toString(bribedata[position].getSatis()));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(bribedata[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return bribedata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewSatis;
        public TextView textViewAmount;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.bribe_item_name_textview);
            this.textViewSatis = itemView.findViewById(R.id.bribe_item_satis_textview);
            this.textViewAmount = itemView.findViewById(R.id.bribe_item_amount_textview);
            linearLayout = itemView.findViewById(R.id.bribe_item_linerLayout);
        }
    }
}