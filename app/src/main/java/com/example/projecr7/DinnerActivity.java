package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.projecr7.CalculateTableAlgorithm.MainAlgorithm;
import com.example.projecr7.bribes.BribeListActivity;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.forceAssign.ForceAssignActivity;
import com.example.projecr7.makeit.MakeItMainActivity;
import com.example.projecr7.makeit.ViewDialog;
import com.example.projecr7.peoplelist.PeopleListActivity;
import com.example.projecr7.proximity.proximityListActivity;
import com.example.projecr7.tablelist.TableListActivity;

import java.util.concurrent.TimeUnit;

public class DinnerActivity extends AppCompatActivity {

    private int dinnerId;

    private ViewDialog viewDialog;

    private MainAlgorithm algo;

    private static final String TAG = "DinnerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX,0);

        TextView textview = findViewById(R.id.textViewDinnerName);
        textview.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId).toString());

        viewDialog = new ViewDialog(this);

        Button makeitBtn = findViewById(R.id.button_makeIt);
        makeitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMakeIt();
            }
        });

//        LinearLayout goT = findViewById(R.id.go_table_list);
//        goT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DinnerActivity.this, TableListActivity.class);
//                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
//                startActivity(intent);
//            }
//        });
//        TextView textView = findViewById(R.id.textView_dinner_id);
//        textView.setText(Integer.toString(dinnerId));
    }

    // when pressing people list
    public void goPeopleList(View view) {
        Intent intent = new Intent(this, PeopleListActivity.class);
        intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
        startActivity(intent);
    }

    public void goTableList(View view){
        Intent intent = new Intent(this, TableListActivity.class);
        intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
        startActivity(intent);
    }

    public void goProximityList(View view){
        Intent intent = new Intent(this, proximityListActivity.class);
        intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
        startActivity(intent);
    }

    public void goBribeList(View view){
        Intent intent = new Intent(this, BribeListActivity.class);
        intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
        startActivity(intent);
    }

    public void goForce(View view){
        Intent intent = new Intent(this, ForceAssignActivity.class);
        intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
        startActivity(intent);
    }

    public void goMakeIt() {
        algo = new MainAlgorithm(dinnerId);
        if(!algo.checkDinnerSize())
            return;
        viewDialog.showDialog();
        Log.i(TAG, "start algo========= ");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                long startT = System.nanoTime();
                algo.start(false);
                long elapsedTime = System.nanoTime() - startT;
                Log.i(TAG, "Time used for inserting proximity: " + (double) elapsedTime / 1_000_000_000);
                viewDialog.hideDialog();
                Intent intent = new Intent(DinnerActivity.this, MakeItMainActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        }, 10);
        //viewDialog.hideDialog();

    }

}
