package com.example.projecr7.bribes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;

import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.onClickInterface;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class BribeListActivity extends AppCompatActivity {

    private int dinnerId;

    private List<Bribe> bribeList;

    private RecyclerView mRecyclerView;
    private MyBribeListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private onClickInterface mOnClickInterface;

    public static final String EXTRA_BRIBEID = "com.example.projecr7.bribeId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bribe_list);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);

        TextView textview = findViewById(R.id.textViewDinnerName_bribeList);
        textview.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId).toString());

        mRecyclerView = findViewById(R.id.recyclerView_bribe);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Bribe selectBribe = (Bribe)i;
                int selectBribeId = selectBribe.getUid();
                Intent intent = new Intent(BribeListActivity.this, ManageBribeActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, selectBribeId);
                intent.putExtra(BribeListActivity.EXTRA_BRIBEID, selectBribe.getUid());
                startActivity(intent);
            }
        };

        updateBribeList();



        FloatingActionButton fab = findViewById(R.id.fab_bribe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BribeListActivity.this, AddBribeActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });
    }

    private void updateBribeList() {
        bribeList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().bribeDao().loadAllByDinner(dinnerId);
        Bribe[] bribeArray;;
        if(bribeList.size() > 0){
            bribeArray =  new Bribe[bribeList.size()];
            for(int i = 0; i < bribeList.size(); i++){
                bribeArray[i] = bribeList.get(i);
            }
            mAdapter = new MyBribeListAdapter(bribeArray, mOnClickInterface);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
