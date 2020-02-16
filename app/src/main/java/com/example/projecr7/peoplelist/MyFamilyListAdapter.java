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
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;

import java.util.List;

public class MyFamilyListAdapter extends RecyclerView.Adapter<com.example.projecr7.peoplelist.MyFamilyListAdapter.ViewHolder>{
    private Family[] familyData;
    private onClickInterface mOnClickInterface;

    // RecyclerView recyclerView;
    public MyFamilyListAdapter(Family[] familyData, onClickInterface mOnClickInterface) {
        this.familyData = familyData;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public com.example.projecr7.peoplelist.MyFamilyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.family_list_item, parent, false);
        com.example.projecr7.peoplelist.MyFamilyListAdapter.ViewHolder viewHolder = new com.example.projecr7.peoplelist.MyFamilyListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.example.projecr7.peoplelist.MyFamilyListAdapter.ViewHolder holder, final int position) {
        List<Person> family = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByFamily(familyData[position].getUid());
        holder.textView1.setText(family.get(0).getName());
        holder.textView2.setText(family.get(1).getName());
        holder.textView3.setText(family.get(2).getName());
        if(family.size() == 3)
            holder.textView4.setText("");
        else if(family.size() == 4)
            holder.textView4.setText(family.get(3).getName());
        else
            holder.textView4.setText("...");
        holder.imageView.setImageResource(R.drawable.family_icon);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(familyData[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return familyData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView_table_family);
            this.textView1 = itemView.findViewById(R.id.textView_table_family1);
            this.textView2 = itemView.findViewById(R.id.textView_table_family2);
            this.textView3 = itemView.findViewById(R.id.textView_table_family3);
            this.textView4 = itemView.findViewById(R.id.textView_table_family4);
            linearLayout = itemView.findViewById(R.id.LinerLayout_table_family);
        }
    }
}
