package com.example.projecr7.peoplelist;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;

import java.util.List;

public class MyAddFamilyAdapter extends RecyclerView.Adapter<MyAddFamilyAdapter.ViewHolder> {
    private int familySize;
    private OnEditTextChanged onEditTextChanged;
    private OnSpinnerChanged onSpinnerChanged;

    public MyAddFamilyAdapter(int familySize, OnEditTextChanged onEditTextChanged, OnSpinnerChanged onSpinnerChanged) {
        this.familySize = familySize;
        this.onEditTextChanged = onEditTextChanged;
        this.onSpinnerChanged = onSpinnerChanged;
    }

    @Override
    public com.example.projecr7.peoplelist.MyAddFamilyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.add_family_list_item, parent, false);
        com.example.projecr7.peoplelist.MyAddFamilyAdapter.ViewHolder viewHolder = new com.example.projecr7.peoplelist.MyAddFamilyAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.example.projecr7.peoplelist.MyAddFamilyAdapter.ViewHolder holder, final int position) {
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onEditTextChanged.onTextChanged(position, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        String[] arraySpinner = new String[3];
        arraySpinner[0] = "male";
        arraySpinner[1] = "female";
        arraySpinner[2] = "other";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                if(position1 == 0)
                    onSpinnerChanged.onSelectChanged(position, "male");
                else if(position1 == 1)
                    onSpinnerChanged.onSelectChanged(position, "female");
                else
                    onSpinnerChanged.onSelectChanged(position, "other");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        int i = position + 1;
        holder.textview.setText("Guest " + i);
    }


    @Override
    public int getItemCount() {
        return familySize;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textview;
        public EditText editText;
        public Spinner spinner;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.editText = itemView.findViewById(R.id.add_person_name_input_family);
            this.spinner = itemView.findViewById(R.id.add_person_gender_spinner_family);
            this.textview = itemView.findViewById(R.id.family_member_hint);
            linearLayout = itemView.findViewById(R.id.LinerLayout_add_family);
        }
    }
}
