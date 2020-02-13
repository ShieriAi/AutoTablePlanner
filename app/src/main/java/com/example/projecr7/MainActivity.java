package com.example.projecr7;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static Context mContext;
//    private AppDatabase db;
//    private DinnerDao mDinnerDao;
    private List<Dinner> allDinner;
    private onClickInterface mOnClickInterface;
    private int selectDinnerId;
    public static final String EXTRA_INDEX = "com.example.projecr7.INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        db = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "dinnerDatabase").allowMainThreadQueries().build();
//        mDinnerDao = db.dinnerDao();
        allDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().getAll();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Dinner", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Dinner dinnerT = new Dinner(allDinner.size(), "nameTest");
//                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().insert(dinnerT);
//                updateDinnerList();
                Intent intent = new Intent(MainActivity.this,AddDinnerActivity.class);
                startActivity(intent);
            }
        });

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Dinner selectDinner = (Dinner)i;
                selectDinnerId = selectDinner.getUid();
                Intent intent = new Intent(MainActivity.this,DinnerActivity.class);
                intent.putExtra(EXTRA_INDEX, selectDinnerId);
                startActivity(intent);
             }
        };


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        updateDinnerList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ManageDinnerActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSelectDinnerId(int selectDinnerId) {
        this.selectDinnerId = selectDinnerId;
    }

    // when pressing a dinner
//    public void goDinner(View view) {
//        Intent intent = new Intent(this, DinnerActivity.class);
//        startActivity(intent);
//    }

    public static Context getContext() {
        return mContext;
    }

    public void goAddDinner(View view) {
        Intent intent = new Intent(this, AddDinnerActivity.class);
        startActivity(intent);
    }

    public void updateDinnerList(){
        allDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().getAll();
        Dinner[] dinnerSet;
        if(allDinner.size() != 0){
            dinnerSet =  new Dinner[allDinner.size()];
            for(int i = 0; i < allDinner.size(); i++){
                dinnerSet[i] = allDinner.get(i);
            }
            mAdapter = new MyAdapter(dinnerSet, mOnClickInterface);
            recyclerView.setAdapter(mAdapter);
        }
    }
}
