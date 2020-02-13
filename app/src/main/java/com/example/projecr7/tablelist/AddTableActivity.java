package com.example.projecr7.tablelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.projecr7.AddPersonActivity;
import com.example.projecr7.MainActivity;
import com.example.projecr7.PeopleListActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Table;

public class AddTableActivity extends AppCompatActivity {

    private int dinnerId;
    private NumberPicker tableSizeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        Button addTableBtn = findViewById(R.id.button_add_table_with_name);
        final EditText tableNameInput = findViewById(R.id.add_table_name_input);
        final TextView tableSizeHint = findViewById(R.id.add_table_size_hint);
        tableSizeInput = findViewById(R.id.add_table_size_input);
        tableSizeInput.setMinValue(2);
        tableSizeInput.setMaxValue(20);
        tableSizeInput.setWrapSelectorWheel(true);

        tableSizeInput.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tableSizeHint.setText("Table size : " + newVal);
            }
        });

        addTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = tableNameInput.getText().toString();
                int tableSize = tableSizeInput.getValue();
                if (tableName.matches("")) {
                    return;
                }
                Table newTable = new Table(tableName, tableSize);
                newTable.setDinnerId(dinnerId);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().insert(newTable);
                Intent intent = new Intent(AddTableActivity.this, TableListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });


    }
}
