package com.example.projecr7.tablelist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Table;
import com.example.projecr7.onClickInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

class SortbyTableType implements Comparator<Table>
{
    @Override
    public int compare(Table o1, Table o2) {
        return o1.getTableType() - o2.getTableType();
    }
}

public class TableListActivity extends AppCompatActivity {

    private static final String TAG = "TableListActivity";

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
                startActivityForResult(intent, 1);
            }
        };

        updateTableList();

        FloatingActionButton fab = findViewById(R.id.fab_table);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TableListActivity.this, AddTableActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode ==  RESULT_OK){
                dinnerId = data.getIntExtra(MainActivity.EXTRA_INDEX, 4);
                updateTableList();
            }
        }
    }

    private void updateTableList(){
        tableList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Collections.sort(tableList, new SortbyTableType());
        Table[] tableArray = new Table[0];
        Log.i(TAG, "updating table list: " + tableList.size());
        if(tableList.size() > 0){
            tableArray =  new Table[tableList.size()];
            for(int i = 0; i < tableList.size(); i++){
                tableArray[i] = tableList.get(i);
            }
        }
        mAdapter = new MyTableListAdapter(tableArray, mOnClickInterface);
        mRecyclerView.setAdapter(mAdapter);
    }
}
