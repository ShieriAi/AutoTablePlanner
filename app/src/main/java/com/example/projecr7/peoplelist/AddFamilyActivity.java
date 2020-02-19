package com.example.projecr7.peoplelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;

public class AddFamilyActivity extends AppCompatActivity {
    private int dinnerId;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int familySize;
    private String[] enteredStrings = new String[18];
    private String[] enteredGenders = new String[18];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        for(int i = 0; i < 18; i++){
            enteredStrings[i] = "";
            enteredGenders[i] = "male";
        }

        mRecyclerView = findViewById(R.id.my_recycler_view_family_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Spinner mSpinner = findViewById(R.id.add_family_size_spinner);
        String[] arraySpinner = new String[18];
        for(int i = 0; i < 18; i++){
            arraySpinner[i] = Integer.toString(i + 3);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        familySize = 3;

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < 18; i++){
                    enteredStrings[i] = "";
                    enteredGenders[i] = "male";
                }
                familySize = position + 3;
                updateRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                for(int i = 0; i < 18; i++){
                    enteredStrings[i] = "";
                    enteredGenders[i] = "male";
                }
                familySize = 3;
                updateRecyclerView();
            }
        });

        updateRecyclerView();

        Button addFamilyBtn = findViewById(R.id.button_add_family);
        addFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < familySize; i++){
                    if(enteredStrings[i].matches(""))
                        return;
                }
                Family newFamily = new Family(familySize);
                newFamily.setDinnerId(dinnerId);
                newFamily.setDisplayName(enteredStrings[0] + ", " + enteredStrings[1] + "...");
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().insert(newFamily);
                for(int i = 0; i < familySize; i++){
                    Person newPerson = new Person(enteredStrings[i],enteredGenders[i]);
                    newPerson.setOtherId(i);
                    newPerson.setDinnerId(dinnerId);
                    newPerson.setFamilyId(newFamily.getUid());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().insert(newPerson);
                }
                Intent intent = new Intent(AddFamilyActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });
    }

    private void updateRecyclerView() {
        mAdapter = new MyAddFamilyAdapter(familySize, new OnEditTextChanged() {
            @Override
            public void onTextChanged(int position, String name) {
                enteredStrings[position] = name;
            }
        }, new OnSpinnerChanged() {
            @Override
            public void onSelectChanged(int position, String gender) {
                enteredGenders[position] = gender;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
