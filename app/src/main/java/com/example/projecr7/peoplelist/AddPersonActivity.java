package com.example.projecr7.peoplelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;

public class AddPersonActivity extends AppCompatActivity {

    private int dinnerId;

    private final String TAG = "AddPersonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        Button addPersonBtn = findViewById(R.id.button_add_person_with_name);
        Button addMutiBtn = findViewById(R.id.button_add_person_muti);
        final EditText personNameInput = findViewById(R.id.add_person_name_input);
        final Spinner personGenderSpinner = findViewById(R.id.add_person_gender_spinner);
        final EditText mutiInput = findViewById(R.id.editText_muti_input);

        String[] arraySpinner = new String[3];
        arraySpinner[0] = "male";
        arraySpinner[1] = "female";
        arraySpinner[2] = "other";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personGenderSpinner.setAdapter(adapter);
        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personName = personNameInput.getText().toString();
                String personGender = personGenderSpinner.getSelectedItem().toString();
                if (personName.matches("")) {
                    return;
                }
                Person newPerson = new Person(personName, personGender);
                newPerson.setDinnerId(dinnerId);
                newPerson.setTableId(4);
                newPerson.setCoupleId(4);
                newPerson.setSeatId(-1);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().insert(newPerson);
                Intent intent = new Intent(AddPersonActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });
        addMutiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mutiInput.getText().toString();
                String[] peopleLine = inputText.split("\\n");
                String[][] people = new String[peopleLine.length][2];
                for(int i = 0; i < peopleLine.length; i++){
                    if(!peopleLine[i].contains(","))
                        return;
                    people[i] = peopleLine[i].split(",", 2);
                    if(people[i][1].contains("female"))
                        people[i][1] = "female";
                    else if(people[i][1].contains("male"))
                        people[i][1] = "male";
                    else if(people[i][1].contains("other"))
                        people[i][1] = "other";
                    else
                        return;
                    Log.i(TAG, "MutiInput:" + people[i][0] + "/" + people[i][1] + ";");
                }
                for(int i = 0; i < peopleLine.length; i++){
                    Person newPerson = new Person(people[i][0], people[i][1]);
                    newPerson.setDinnerId(dinnerId);
                    newPerson.setTableId(4);
                    newPerson.setCoupleId(4);
                    newPerson.setSeatId(-1);
                    newPerson.setOtherId(i);
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().insert(newPerson);
                }
                Intent intent = new Intent(AddPersonActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });
    }
}
