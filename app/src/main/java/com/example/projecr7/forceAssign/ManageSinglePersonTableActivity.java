package com.example.projecr7.forceAssign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.TableView;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Table;

import java.util.List;

public class ManageSinglePersonTableActivity extends AppCompatActivity {

    private static final String TAG = "ManageSinglePersonTableActivity";

    private int dinnerId, tableSelect, seatSelect;
    private Long personId;
    private Person currentPerson;
    private List<Table> tables;
    private String[] seatArraySpinner;
    private ArrayAdapter<String> seatAdapter;
    private Spinner seatSpinner;

    private LinearLayout drawTableboard;
    private TableView tableDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_single_person_table);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        personId = intent.getLongExtra(ForceAssignActivity.EXTRA_ID, 4);

        TextView nameDisplay = findViewById(R.id.single_person_table_name);
        currentPerson = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadSingleById(personId);
        nameDisplay.setText(currentPerson.getName());
        tables = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Log.i(TAG, Integer.toString(tables.size()));
//        List<Person> personlist = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
//        for(int i = 0; i < )

        Spinner tableSpinner = findViewById(R.id.single_select_table_spinner);
        seatSpinner = findViewById(R.id.single_select_seat_spinner);
        drawTableboard = findViewById(R.id.table_display_draw_add_person);

        String[] tableArraySpinner = new String[(tables.size() + 1)];
        tableArraySpinner[0] = "no assign table";
        int tableIndex = 0;
        for(int i = 1; i <= tables.size(); i++ ){
            if(currentPerson.getTableId() == tables.get(i - 1).getUid())
                tableIndex = i;
            tableArraySpinner[i] = tables.get(i - 1).getTableName() + "  Size: " + tables.get(i - 1).getTableSize();
        }

        ArrayAdapter<String> tableAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tableArraySpinner);
        tableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(tableAdapter);

        Log.i(TAG, "tableIndex=" +tableIndex + currentPerson.getName() + " tableId: " + currentPerson.getTableId() + "===" + currentPerson.getSeatId());

        if(currentPerson.getSeatId() != -1 && tableIndex != 0){
            seatArraySpinner = new String[tables.get(tableIndex-1).getTableSize()];
            for (int i = 0; i < tables.get(tableIndex-1).getTableSize(); i++) {
                seatArraySpinner[i] = "seat: " + Integer.toString(i + 1);
            }
        }
        seatArraySpinner = new String[0];
        seatAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, seatArraySpinner);
        seatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatSpinner.setAdapter(seatAdapter);


        if(currentPerson.getSeatId() != -1 && currentPerson.isLock()) {
            tableSpinner.setSelection(tableIndex);
            seatSpinner.setSelection(currentPerson.getSeatId());
        }
        tableDraw = new TableView(ManageSinglePersonTableActivity.this, 0, -1, null, false);
        drawTableboard.addView(tableDraw);
        tableSelect = -1;
        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position--;
                if(position >= 0) {
                    Table currentTable = tables.get(position);
                    tableSelect = position;
                    seatArraySpinner = new String[currentTable.getTableSize()];
                    for (int i = 0; i < currentTable.getTableSize(); i++) {
                        seatArraySpinner[i] = "seat: " + Integer.toString(i + 1);
                    }
                    tableDraw.update(currentTable.getTableSize(), currentTable.getTableType(), currentTable.getTableName());
                    tableDraw.invalidate();
                }
                else {
                    tableSelect = -1;
                    seatArraySpinner = new String[0];
                    tableDraw.update(0, -1, null);
                    tableDraw.invalidate();
                }
                seatAdapter = new ArrayAdapter<String>(ManageSinglePersonTableActivity.this,
                        android.R.layout.simple_spinner_item, seatArraySpinner);
                seatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                seatSpinner.setAdapter(seatAdapter);
                if(position>=0)
                    if(currentPerson.getSeatId() != -1 && tables.get(position).getUid() == currentPerson.getTableId() && currentPerson.isLock()) {
                        seatSpinner.setSelection(currentPerson.getSeatId());
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button saveBtn = findViewById(R.id.button_save_force_assign);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tableSelect == -1){
                    currentPerson.setTableId(4);
                    currentPerson.setSeatId(-1);
                    currentPerson.setLock(false);
                }
                else {
                    currentPerson.setTableId(tables.get(tableSelect).getUid());
                    currentPerson.setSeatId(seatSpinner.getSelectedItemPosition());
                    currentPerson.setLock(true);
                }
                Log.i(TAG, currentPerson.getTableId() + "---" + currentPerson.getSeatId());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().updateUsers(currentPerson);
                Intent intent = new Intent(com.example.projecr7.forceAssign.ManageSinglePersonTableActivity.this, ForceAssignActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
