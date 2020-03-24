package com.example.projecr7.makeit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.TableView;
import com.example.projecr7.bribes.BribeListActivity;
import com.example.projecr7.bribes.ManageBribeActivity;
import com.example.projecr7.bribes.MyBribeListAdapter;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Table;
import com.example.projecr7.forceAssign.ManageSinglePersonTableActivity;
import com.example.projecr7.onClickInterface;

import java.util.Arrays;
import java.util.List;

public class SingleTableResultActivity extends AppCompatActivity {

    private static final String TAG = "SingleTableResultActivity";

    private int tableId;
    private List<Person> personList;
    private String[] seatsNameDisplay;
    private double[] personScoreDisplay;
    private Table currentTable;

    private RecyclerView mRecyclerView;
    private MyTableResultAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout drawTableboard;
    private TableView tableDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_table_result);

        Intent intent = getIntent();
        tableId = intent.getIntExtra(MakeItMainActivity.EXTRA_TABLEID, 4);

        personList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByTable(tableId);
        currentTable = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadSingleById(tableId);

        TextView tableNameDisplay = findViewById(R.id.textViewTableName_result);
        tableNameDisplay.setText(currentTable.getTableName());

        drawTableboard = findViewById(R.id.table_result_display);
        tableDraw = new TableView(SingleTableResultActivity.this, currentTable.getTableSize(), currentTable.getTableType(), currentTable.getTableName(), true);
        drawTableboard.addView(tableDraw);

        seatsNameDisplay = new String[currentTable.getTableSize()];
        personScoreDisplay = new double[currentTable.getTableSize()];

        Arrays.fill(seatsNameDisplay, "");
        Arrays.fill(personScoreDisplay, 0);

        for(int i = 0; i < personList.size(); i++){
            Log.i(TAG, personList.get(i).getName() + "Table: " + personList.get(i).getTableId() + "Seat: " + personList.get(i).getSeatId());
            seatsNameDisplay[personList.get(i).getSeatId()] = personList.get(i).getName();
            personScoreDisplay[personList.get(i).getSeatId()] = personList.get(i).getHappiness();
        }

        mRecyclerView = findViewById(R.id.recyclerView_single_table_result);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyTableResultAdapter(seatsNameDisplay, personScoreDisplay);
        mRecyclerView.setAdapter(mAdapter);
    }

}
