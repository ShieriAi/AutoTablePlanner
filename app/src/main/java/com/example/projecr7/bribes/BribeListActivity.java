package com.example.projecr7.bribes;

import androidx.annotation.Nullable;
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
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(BribeListActivity.EXTRA_BRIBEID, selectBribeId);
                startActivityForResult(intent,1);
            }
        };

        updateBribeList();



        FloatingActionButton fab = findViewById(R.id.fab_bribe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BribeListActivity.this, AddBribeActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode ==  RESULT_OK){
                dinnerId = data.getIntExtra(MainActivity.EXTRA_INDEX, 4);
                updateBribeList();
            }
        }
    }

    private void updateBribeList() {
        bribeList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().bribeDao().loadAllByDinner(dinnerId);
        Bribe[] bribeArray = new Bribe[0];
        if(bribeList.size() > 0){
            bribeArray =  new Bribe[bribeList.size()];
            for(int i = 0; i < bribeList.size(); i++){
                bribeArray[i] = bribeList.get(i);
            }
        }
        mAdapter = new MyBribeListAdapter(bribeArray, mOnClickInterface);
        mRecyclerView.setAdapter(mAdapter);
    }
}
