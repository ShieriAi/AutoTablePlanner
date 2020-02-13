package com.example.projecr7.peoplelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;

public class AddCoupleActivity extends AppCompatActivity {

    private int dinnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_couple);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        Button addPersonBtn = findViewById(R.id.button_add_couple_with_name);
        final EditText couple1NameInput = findViewById(R.id.add_couple1_name_input);
        final Spinner couple1GenderSpinner = findViewById(R.id.add_couple1_gender_spinner);
        final EditText couple2NameInput = findViewById(R.id.add_couple2_name_input);
        final Spinner couple2GenderSpinner = findViewById(R.id.add_couple2_gender_spinner);
        String[] arraySpinner = new String[3];
        arraySpinner[0] = "male";
        arraySpinner[1] = "female";
        arraySpinner[2] = "other";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        couple1GenderSpinner.setAdapter(adapter);
        couple2GenderSpinner.setAdapter(adapter);
        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couple1Name = couple1NameInput.getText().toString();
                String couple1Gender = couple1GenderSpinner.getSelectedItem().toString();
                String couple2Name = couple2NameInput.getText().toString();
                String couple2Gender = couple2GenderSpinner.getSelectedItem().toString();
                if (couple1Name.matches("") ||couple2Name.matches("")) {
                    return;
                }
                Couple newCouple = new Couple();
                newCouple.setDinnerId(dinnerId);
                Person newPerson1 = new Person(couple1Name, couple1Gender);
                newPerson1.setDinnerId(dinnerId);
                newPerson1.setTableId(4);
                newPerson1.setCoupleId(newCouple.getUid());
                Person newPerson2 = new Person(couple2Name, couple2Gender);
                newPerson2.setDinnerId(dinnerId);
                newPerson2.setOtherId();
                newPerson2.setTableId(4);
                newPerson2.setCoupleId(newCouple.getUid());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().insert(newCouple);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().insert(newPerson1);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().insert(newPerson2);
                Intent intent = new Intent(AddCoupleActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

    }
}
