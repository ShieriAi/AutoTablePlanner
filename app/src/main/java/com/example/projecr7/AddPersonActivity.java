package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Person;

public class AddPersonActivity extends AppCompatActivity {

    private int dinnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        Button addPersonBtn = findViewById(R.id.button_add_person_with_name);
        final EditText personNameInput = findViewById(R.id.add_person_name_input);
        final Spinner personGenderSpinner = findViewById(R.id.add_person_gender_spinner);
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
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().insert(newPerson);
                Intent intent = new Intent(AddPersonActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });
    }
}
