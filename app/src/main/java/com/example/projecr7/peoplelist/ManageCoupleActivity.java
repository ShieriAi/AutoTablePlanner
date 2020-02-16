package com.example.projecr7.peoplelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

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

import java.util.ArrayList;
import java.util.List;

public class ManageCoupleActivity extends AppCompatActivity {

    private int coupleId;
    private int dinnerId;
    private Couple currentCouple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_couple);

        Intent intent = getIntent();
        coupleId = intent.getIntExtra(PeopleListActivity.EXTRA_COUPLEID, 0);
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        currentCouple = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().loadSingleById(coupleId);
        Button editCoupleBtn = findViewById(R.id.button_edit_couple_with_name);
        Button deleteCoupleBtn = findViewById(R.id.button_delete_couple);
        final EditText couple1NameInput = findViewById(R.id.edit_couple1_name_input);
        final Spinner couple1GenderSpinner = findViewById(R.id.edit_couple1_gender_spinner);
        final EditText couple2NameInput = findViewById(R.id.edit_couple2_name_input);
        final Spinner couple2GenderSpinner = findViewById(R.id.edit_couple2_gender_spinner);
        String[] arraySpinner = new String[3];
        arraySpinner[0] = "male";
        arraySpinner[1] = "female";
        arraySpinner[2] = "other";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        couple1GenderSpinner.setAdapter(adapter);
        couple2GenderSpinner.setAdapter(adapter);

        List<Person> people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        final Person[] couple = new Person[2];
        int j = 0;
        for(int i = 0; i < people.size(); i++){
            if(people.get(i).getCoupleId() == coupleId) {
                couple[j] = people.get(i);
                j++;
            }
        }

        if(couple[0].getGender().contains("female")) {
            couple1GenderSpinner.setSelection(1);
        }
        else if(couple[0].getGender().contains("male")){
            couple1GenderSpinner.setSelection(0);
        }
        else
            couple1GenderSpinner.setSelection(2);
        if(couple[1].getGender().contains("female")) {
            couple2GenderSpinner.setSelection(1);
        }
        else if(couple[1].getGender().contains("male")){
            couple2GenderSpinner.setSelection(0);
        }
        else
            couple2GenderSpinner.setSelection(2);
        couple1NameInput.setText(couple[0].getName());
        couple2NameInput.setText(couple[1].getName());

        editCoupleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couple1Name = couple1NameInput.getText().toString();
                String couple1Gender = couple1GenderSpinner.getSelectedItem().toString();
                String couple2Name = couple2NameInput.getText().toString();
                String couple2Gender = couple2GenderSpinner.getSelectedItem().toString();
                if (couple1Name.matches("") ||couple2Name.matches("")) {
                    return;
                }
                if(couple1Name != couple[0].getName() || couple1Gender != couple[0].getGender()) {
                    Person newPerson1 = new Person(couple1Name, couple1Gender);
                    newPerson1.setId(couple[0].getId());
                    newPerson1.setDinnerId(couple[0].getDinnerId());
                    newPerson1.setTableId(couple[0].getTableId());
                    newPerson1.setCoupleId(coupleId);
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().updateUsers(newPerson1);
                }
                if(couple1Name != couple[1].getName() || couple1Gender != couple[1].getGender()) {
                    Person newPerson2 = new Person(couple2Name, couple2Gender);
                    newPerson2.setId(couple[1].getId());
                    newPerson2.setDinnerId(couple[1].getDinnerId());
                    newPerson2.setCoupleId(coupleId);
                    newPerson2.setTableId(couple[1].getTableId());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().updateUsers(newPerson2);
                }
                Intent intent = new Intent(ManageCoupleActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

        deleteCoupleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCoupleActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().delete(currentCouple);
                startActivity(intent);
               //TODO NavUtils.navigateUpFromSameTask(ManageCoupleActivity.this);
            }
        });
    }
}
