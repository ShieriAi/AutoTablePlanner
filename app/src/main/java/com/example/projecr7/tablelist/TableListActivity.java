package com.example.projecr7.tablelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.projecr7.AddPersonActivity;
import com.example.projecr7.MainActivity;
import com.example.projecr7.ManagePersonActivity;
import com.example.projecr7.MyPeopleListAdapter;
import com.example.projecr7.PeopleListActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Table;
import com.example.projecr7.onClickInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TableListActivity extends AppCompatActivity {

    private int dinnerId;
    private List<Table> tableList;

    private RecyclerView mRecyclerView;
    private MyTableListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private onClickInterface mOnClickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        TextView textview = findViewById(R.id.textViewDinnerName_tableList);
        textview.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId).toString());

        mRecyclerView = findViewById(R.id.recyclerView_table);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Table selectTable = (Table)i;
                int selectTableId = selectTable.getUid();
                Intent intent = new Intent(TableListActivity.this, ManageTableActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, selectTableId);
                startActivity(intent);
            }
        };

        updateTableList();

        FloatingActionButton fab = findViewById(R.id.fab_table);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TableListActivity.this, AddTableActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });


    }

    private void updateTableList(){
        tableList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Table[] tableArray;;
        if(tableList.size() - 1 > 0){
            tableArray =  new Table[tableList.size() - 1];
            for(int i = 0; i < tableList.size(); i++){
                if(tableList.get(i).getUid() != 4)
                    tableArray[i] = tableList.get(i);
            }
            mAdapter = new MyTableListAdapter(tableArray, mOnClickInterface);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
