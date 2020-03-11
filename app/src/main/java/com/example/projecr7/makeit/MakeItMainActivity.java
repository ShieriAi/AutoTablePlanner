package com.example.projecr7.makeit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.projecr7.CalculateTableAlgorithm.MainAlgorithm;
import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Table;
import com.example.projecr7.onClickInterface;
import com.example.projecr7.tablelist.ManageTableActivity;
import com.example.projecr7.tablelist.MyTableListAdapter;
import com.example.projecr7.tablelist.TableListActivity;

import java.util.List;

public class MakeItMainActivity extends AppCompatActivity {

    public static final String EXTRA_TABLEID = "com.example.projecr7.tableID";

    private int dinnerId;
    private TextView dinnerNameTextview;

    private Dinner currentDinner;
    private List<Table> tableList;

    private RecyclerView mRecyclerView;
    private MyTableListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView scoreTextview;

    private onClickInterface mOnClickInterface;

    private ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_it_main);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);
        currentDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId);

        TextView textview = findViewById(R.id.textViewDinnerName_makeit);
        textview.setText(currentDinner.toString() + " Result");

        scoreTextview = findViewById(R.id.textViewScore_makeit);
        scoreTextview.setText("Current score = " + currentDinner.score);

        tableList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);

        mRecyclerView = findViewById(R.id.recyclerView_makeit);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Table selectTable = (Table)i;
                int selectTableId = selectTable.getUid();
                Intent intent = new Intent(MakeItMainActivity.this, SingleTableResultActivity.class);
                intent.putExtra(MakeItMainActivity.EXTRA_TABLEID, selectTableId);
                startActivity(intent);
            }
        };

        viewDialog = new ViewDialog(this);

        Button retryBtn = findViewById(R.id.button_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDialog.showDialog();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainAlgorithm mainAlgorithm = new MainAlgorithm(dinnerId);
                        mainAlgorithm.retry();
                        updateTableList();
                        updateScore();
                        viewDialog.hideDialog();
                    }
                }, 500);
            }
        });

        updateTableList();
    }

    private void updateScore(){
        currentDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId);
        scoreTextview.setText("Current score = " + currentDinner.score);
    }

    private void updateTableList(){
        tableList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Table[] tableArray;
        if(tableList.size() > 0){
            tableArray =  new Table[tableList.size()];
            for(int i = 0; i < tableList.size(); i++){
                tableArray[i] = tableList.get(i);
            }
            mAdapter = new MyTableListAdapter(tableArray, mOnClickInterface);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
