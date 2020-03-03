package com.example.projecr7.forceAssign;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.R;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;

public class ForceAssignPeopleListAdapter extends RecyclerView.Adapter<com.example.projecr7.forceAssign.ForceAssignPeopleListAdapter.ViewHolder> {
    private Person[] people;
    private onClickInterface mOnClickInterface;

    // RecyclerView recyclerView;
    public ForceAssignPeopleListAdapter(Person[] people, onClickInterface mOnClickInterface) {
        this.people = people;
        this.mOnClickInterface = mOnClickInterface;
    }
    @Override
    public com.example.projecr7.forceAssign.ForceAssignPeopleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.my_text_view, parent, false);
        com.example.projecr7.forceAssign.ForceAssignPeopleListAdapter.ViewHolder viewHolder = new com.example.projecr7.forceAssign.ForceAssignPeopleListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.example.projecr7.forceAssign.ForceAssignPeopleListAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(people[position].getName());
        if(people[position].getFamilyId() != 4)
            holder.imageView.setImageResource(R.drawable.family_icon);
        else if(people[position].getCoupleId() != 4)
            holder.imageView.setImageResource(R.drawable.couple_icon);
        else
            holder.imageView.setImageResource(R.drawable.single_icon);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickInterface.setClick(people[position]);
            }
        });
    }


    @Override
    public int getItemCount() {
        return people.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView, textView_table, textView_seat;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView_single);
            this.textView = itemView.findViewById(R.id.textView_single_name);
            this.textView_table = itemView.findViewById(R.id.textView_single_table);
            this.textView_table = itemView.findViewById(R.id.textView_single_table);
            linearLayout = itemView.findViewById(R.id.LinerLayout_single_person);
        }
    }

}
