package com.example.projecr7.tablelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Table;
import com.google.android.material.snackbar.Snackbar;

public class ManageTableActivity extends AppCompatActivity {

    private NumberPicker tableSizeInput;
    private int tableId;
    private Table currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_table);

        Intent intent = getIntent();
        tableId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        currentTable = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadSingleById(tableId);

        Button saveTableBtn = findViewById(R.id.button_edit_table_with_name);
        Button deleteTableBtn = findViewById(R.id.button_delete_table);
        final EditText tableNameInput = findViewById(R.id.edit_table_name_input);
        final TextView tableSizeHint = findViewById(R.id.edit_table_size_hint);
        tableSizeInput = findViewById(R.id.edit_table_size_input);
        tableSizeInput.setMinValue(2);
        tableSizeInput.setMaxValue(20);
        tableSizeInput.setWrapSelectorWheel(true);
        tableSizeInput.setValue(currentTable.getTableSize());
        tableNameInput.setText(currentTable.getTableName());
        tableSizeHint.setText("Size of table: " + currentTable.getTableSize());

        tableSizeInput.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tableSizeHint.setText("Size of table: " + newVal);
            }
        });

        saveTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = tableNameInput.getText().toString();
                int tableSize = tableSizeInput.getValue();
                if (tableName.matches("")) {
                    return;
                }
                if(currentTable.getTableType() != 0 && (tableSize % 2 != 0 || tableSize < 6)){
                    Snackbar.make(findViewById(R.id.manage_table_layout), R.string.table_size_warning, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Table newTable = new Table(tableName, tableSize, currentTable.getTableType());
                newTable.setDinnerId(currentTable.getDinnerId());
                newTable.setScore(currentTable.getScore());
                newTable.setUid(currentTable.getUid());
                Intent intent = new Intent(ManageTableActivity.this, TableListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, currentTable.getDinnerId());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().updateUsers(newTable);
                startActivity(intent);
            }
        });

        deleteTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageTableActivity.this, TableListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, currentTable.getDinnerId());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().delete(currentTable);
                startActivity(intent);
            }
        });


    }
}
