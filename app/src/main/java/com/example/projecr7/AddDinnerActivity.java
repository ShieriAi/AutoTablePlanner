package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Table;

import java.util.List;

public class AddDinnerActivity extends AppCompatActivity {

    private List<Dinner> allDinner;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dinner);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        Button addDinnerWithNamebtn = (Button) findViewById(R.id.button_add_dinner_with_name);
        addDinnerWithNamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTextE = (EditText) findViewById(R.id.dinner_text_input);
                String givenName = nameTextE.getText().toString();
                Dinner newDinner = new Dinner(givenName);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().insert(newDinner);
                // set a default table for people
                Table defaultTable = new Table("default", 10000);
                defaultTable.setUid(4);
                defaultTable.setDinnerId(newDinner.getUid());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().insert(defaultTable);
                Couple defaultCouple = new Couple();
                defaultCouple.setUid(4);
                defaultCouple.setDinnerId(newDinner.getUid());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().insert(defaultCouple);
                Intent intent = new Intent(AddDinnerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
