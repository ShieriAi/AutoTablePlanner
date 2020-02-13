package com.example.projecr7.tablelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.R;
import com.example.projecr7.database.Table;
import com.example.projecr7.onClickInterface;

public class MyTableListAdapter extends RecyclerView.Adapter<MyTableListAdapter.ViewHolder>{
    private Table[] tabledata;
    private onClickInterface mOnClickInterface;

    // RecyclerView recyclerView;
    public MyTableListAdapter(Table[] tabledata, onClickInterface mOnClickInterface) {
        this.tabledata = tabledata;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.table_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //final MyListData myListData = listdata[position];
        holder.textView.setText(tabledata[position].getTableName());
        holder.imageView.setImageResource(R.drawable.table_icon);
        holder.textView_tablesize.setText(Integer.toString(tabledata[position].getTableSize()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(tabledata[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return tabledata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView_tablesize;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView_table);
            this.textView = (TextView) itemView.findViewById(R.id.textView_table);
            this.textView_tablesize = itemView.findViewById(R.id.textView_table_size);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout_table);
        }
    }
}