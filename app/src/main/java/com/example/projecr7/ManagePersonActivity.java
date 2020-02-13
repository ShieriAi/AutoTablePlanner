package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;

public class ManagePersonActivity extends AppCompatActivity {

    private int personId;
    private Person currentPerson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_person);

        Intent intent = getIntent();
        personId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        currentPerson = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadSingleById(personId);

        Button addPersonBtn = findViewById(R.id.button_add_person_with_name);
        final EditText personNameInput = findViewById(R.id.edit_person_name_input);
        final Spinner personGenderSpinner = findViewById(R.id.edit_person_gender_spinner);
        String[] arraySpinner = new String[3];
        arraySpinner[0] = "male";
        arraySpinner[1] = "female";
        arraySpinner[2] = "other";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personGenderSpinner.setAdapter(adapter);
        if(currentPerson.getGender().contains("male")) {
            personGenderSpinner.setSelection(0);
        }
        else if(currentPerson.getGender().contains("female")){
            personGenderSpinner.setSelection(1);
        }
        else
            personGenderSpinner.setSelection(2);
        personNameInput.setText(currentPerson.getName());
        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personName = personNameInput.getText().toString();
                String personGender = personGenderSpinner.getSelectedItem().toString();
                if (personName.matches("")) {
                    return;
                }
                Person newPerson = new Person(personName, personGender);
                newPerson.setId(currentPerson.getId());
                newPerson.setDinnerId(currentPerson.getDinnerId());
                Intent intent = new Intent(ManagePersonActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, currentPerson.getDinnerId());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().updateUsers(newPerson);
                startActivity(intent);
            }
        });
        Button deletePersonBtn = findViewById(R.id.button_delete_person);
        deletePersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePersonActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, currentPerson.getDinnerId());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().delete(currentPerson);
                startActivity(intent);
            }
        });
    }
}