package com.example.projecr7.proximity;

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
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.database.Table;
import com.example.projecr7.onClickInterface;
import com.example.projecr7.tablelist.AddTableActivity;
import com.example.projecr7.tablelist.ManageTableActivity;
import com.example.projecr7.tablelist.MyTableListAdapter;
import com.example.projecr7.tablelist.TableListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ManageProximityActivity extends AppCompatActivity {
    public static final String EXTRA_GUESTID = "com.example.projecr7.guestId";
    public static final String EXTRA_GUESTTYPE = "com.example.projecr7.guestType";

    private int type, dinnerId, guestId;
    private List<Proximity> proximityList;

    private RecyclerView mRecyclerView;
    private MyProximityListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private onClickInterface mOnClickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_proximity);

        Intent intent = getIntent();
        type = intent.getIntExtra(proximityListActivity.EXTRA_TYPE,0);
        guestId = intent.getIntExtra(proximityListActivity.EXTRA_ID, 4);
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);

        mRecyclerView = findViewById(R.id.my_recycler_view_proximity_list);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Proximity selectProximity = (Proximity)i;
                int selectProximityId = selectProximity.getUid();
                Intent intent = new Intent(ManageProximityActivity.this, ManageSingleProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(proximityListActivity.EXTRA_ID, selectProximityId);
                intent.putExtra(ManageProximityActivity.EXTRA_GUESTID, guestId);
                intent.putExtra(ManageProximityActivity.EXTRA_GUESTTYPE, type);
                startActivityForResult(intent,1);
            }
        };

        updateProximityList();

        TextView nameDisplay = findViewById(R.id.proximity_name_hint_textview);
        switch (type){
            case 1:
                nameDisplay.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadSingleById(guestId).getName());
                break;
            case 2:
                nameDisplay.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().loadSingleById(guestId).getDisplayName());
                break;
            case 3:
                nameDisplay.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadSingleById(guestId).getDisplayName());
                break;
        }

        FloatingActionButton fab = findViewById(R.id.fab_main_add_proximity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageProximityActivity.this, AddProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(ManageProximityActivity.EXTRA_GUESTID, guestId);
                intent.putExtra(ManageProximityActivity.EXTRA_GUESTTYPE, type);
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
                type = data.getIntExtra(ManageProximityActivity.EXTRA_GUESTTYPE, 1);
                guestId = data.getIntExtra(proximityListActivity.EXTRA_ID,4);
                updateProximityList();
            }
        }
    }

    public void updateProximityList(){
        proximityList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().proximityDao().loadAllByGuest1(guestId);
        Proximity[] proximityArray = new Proximity[0];
        if(proximityList.size() > 0) {
            proximityArray = new Proximity[proximityList.size()];
            for (int i = 0; i < proximityList.size(); i++) {
                if (proximityList.get(i).getUid() != 4) {
                    proximityArray[i] = proximityList.get(i);
                }
            }
        }
        mAdapter = new MyProximityListAdapter(proximityArray, mOnClickInterface);
        mRecyclerView.setAdapter(mAdapter);
    }
}
