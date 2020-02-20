package com.example.projecr7.makeit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.bribes.BribeListActivity;
import com.example.projecr7.bribes.ManageBribeActivity;
import com.example.projecr7.bribes.MyBribeListAdapter;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Table;
import com.example.projecr7.onClickInterface;

import java.util.List;

public class SingleTableResultActivity extends AppCompatActivity {

    private int tableId;
    private List<Person> personList;
    private String[] seatsNameDisplay;
    private Table currentTable;

    private RecyclerView mRecyclerView;
    private MyTableResultAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_table_result);

        Intent intent = getIntent();
        tableId = intent.getIntExtra(MakeItMainActivity.EXTRA_TABLEID, 4);

        personList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByTable(tableId);
        currentTable = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadSingleById(tableId);;

        seatsNameDisplay = new String[currentTable.getTableSize()];

        for(int i = 0; i < currentTable.getTableSize(); i++){
            seatsNameDisplay[i] = "";
        }
        for(int i = 0; i < personList.size(); i++){
            seatsNameDisplay[personList.get(i).getSeatId()] = personList.get(i).getName();
        }

        mRecyclerView = findViewById(R.id.recyclerView_single_table_result);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyTableResultAdapter(seatsNameDisplay);
        mRecyclerView.setAdapter(mAdapter);
    }

}
