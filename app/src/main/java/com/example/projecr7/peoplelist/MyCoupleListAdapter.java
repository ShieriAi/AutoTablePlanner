package com.example.projecr7.peoplelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;

import java.util.List;

public class MyCoupleListAdapter extends RecyclerView.Adapter<com.example.projecr7.peoplelist.MyCoupleListAdapter.ViewHolder>{
    private Couple[] coupledata;
    private onClickInterface mOnClickInterface;

    // RecyclerView recyclerView;
    public MyCoupleListAdapter(Couple[] coupledata, onClickInterface mOnClickInterface) {
        this.coupledata = coupledata;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public com.example.projecr7.peoplelist.MyCoupleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.couple_list_item, parent, false);
        com.example.projecr7.peoplelist.MyCoupleListAdapter.ViewHolder viewHolder = new com.example.projecr7.peoplelist.MyCoupleListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.example.projecr7.peoplelist.MyCoupleListAdapter.ViewHolder holder, final int position) {
        //final MyListData myListData = listdata[position];
        List<Person> couple = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByCouple(coupledata[position].getUid());
        if(couple.size() >= 2) {
            holder.textView1.setText(couple.get(0).getName());
            holder.textView2.setText(couple.get(1).getName());
        }
        holder.imageView.setImageResource(R.drawable.couple_icon);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(coupledata[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return coupledata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView_table_couple);
            this.textView1 = itemView.findViewById(R.id.textView_table_couple1);
            this.textView2 = itemView.findViewById(R.id.textView_table_couple2);
            linearLayout = itemView.findViewById(R.id.LinerLayout_table_couple);
        }
    }
}
