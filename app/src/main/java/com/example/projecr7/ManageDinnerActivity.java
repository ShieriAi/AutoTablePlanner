package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Person;

import java.util.List;

public class ManageDinnerActivity extends AppCompatActivity {

    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_dinner);

        mSpinner = (Spinner)findViewById(R.id.deleteDinnerSpinner);

        List<Dinner> allDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().getAll();

        Dinner[] arraySpinner = new Dinner[allDinner.size()];
        for(int i = 0; i < allDinner.size(); i++){
            arraySpinner[i] = allDinner.get(i);
        }
        ArrayAdapter<Dinner> adapter = new ArrayAdapter<Dinner>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        Button mButton = findViewById(R.id.button_delete_dinner_with_name);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDinner();
            }
        });

    }

    public void deleteDinner(){
        Dinner selectDinner = (Dinner) mSpinner.getSelectedItem();
        deletePeople(selectDinner.getUid());
        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().delete(selectDinner);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void deletePeople(int dinnerId){
        List<Person> personToDelete = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        for(int i = 0; i < personToDelete.size(); i++){
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().delete(personToDelete.get(i));
        }
    }
}
