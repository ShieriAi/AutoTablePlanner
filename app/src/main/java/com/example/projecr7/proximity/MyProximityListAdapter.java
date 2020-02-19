package com.example.projecr7.proximity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.R;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.onClickInterface;

public class MyProximityListAdapter extends RecyclerView.Adapter<MyProximityListAdapter.ViewHolder>{
    private Proximity[] proximitydata;
    private onClickInterface mOnClickInterface;

    // RecyclerView recyclerView;
    public MyProximityListAdapter(Proximity[] proximitydata, onClickInterface mOnClickInterface) {
        this.proximitydata = proximitydata;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.proximity_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView_Guest1.setText(proximitydata[position].getGuest1String());
        holder.textView_Guest2.setText(proximitydata[position].getGuest2String());
        int proximityType = proximitydata[position].getProximityType();
        switch (proximityType){
            case 1:
                holder.imageView.setImageResource(R.drawable.sad_icon);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.uphappy_icon);
                break;
            case 4:
                holder.imageView.setImageResource(R.drawable.happy_icon);
                break;
            case 5:
                holder.imageView.setImageResource(R.drawable.happyplus_icon);
                break;
        }
        holder.linerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(proximitydata[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return proximitydata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView_Guest1;
        public TextView textView_Guest2;
        public LinearLayout linerLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.proximity_item_imageview);
            this.textView_Guest1 = itemView.findViewById(R.id.proximity_item_me_textview);
            this.textView_Guest2 = itemView.findViewById(R.id.proximity_item_other_textview);
            linerLayout = itemView.findViewById(R.id.poximity_item_linerLayout);
        }
    }
}