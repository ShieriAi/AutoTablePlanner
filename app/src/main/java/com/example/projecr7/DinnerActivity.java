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
import com.example.projecr7.database.Dinner;
import com.example.projecr7.forceAssign.ForceAssignActivity;
import com.example.projecr7.makeit.MakeItMainActivity;
import com.example.projecr7.makeit.ViewDialog;
import com.example.projecr7.peoplelist.PeopleListActivity;
import com.example.projecr7.proximity.proximityListActivity;
import com.example.projecr7.tablelist.TableListActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

public class DinnerActivity extends AppCompatActivity {

    private int dinnerId;

    private ViewDialog viewDialog;

    private MainAlgorithm algo;

    private Dinner currentDinner;

    private static final String TAG = "DinnerActivity";

    public static final String EXTRA_EXIST = "com.example.projecr7.exist";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX,0);

        currentDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId);

        TextView textview = findViewById(R.id.textViewDinnerName);
        textview.setText(currentDinner.toString());

        viewDialog = new ViewDialog(this);

        Button makeitBtn = findViewById(R.id.button_makeIt);
        makeitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMakeIt(false);
            }
        });

        Button seeExistBtn = findViewById(R.id.button_see_exist);
        seeExistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentDinner.isHavePlan()){
                    Log.i(TAG, "showing snackbar");
                    Snackbar.make(findViewById(R.id.dinner_layout), R.string.no_exist_warning, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                goMakeIt(true);
            }
        });

        Log.i(TAG, "have plan? :" + currentDinner.isHavePlan());

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

    public void goMakeIt(final Boolean exist) {
        algo = new MainAlgorithm(dinnerId);
        if(!algo.checkDinnerSize()) {
            Snackbar.make(findViewById(R.id.dinner_layout), R.string.no_seats_warning, Snackbar.LENGTH_SHORT).show();
            return;
        }
        currentDinner.setHavePlan(true);
        viewDialog.showDialog();
        Log.i(TAG, "start algo========= ");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if(!exist) {
                    long startT = System.nanoTime();
                    algo.start(false);
                    long elapsedTime = System.nanoTime() - startT;
                    Log.i(TAG, "Time used for inserting proximity: " + (double) elapsedTime / 1_000_000_000);
                }
                viewDialog.hideDialog();
                Intent intent = new Intent(DinnerActivity.this, MakeItMainActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                if(exist)
                    intent.putExtra(DinnerActivity.EXTRA_EXIST, 1);
                else
                    intent.putExtra(DinnerActivity.EXTRA_EXIST, 0);
                startActivity(intent);
            }
        }, 10);
        //viewDialog.hideDialog();

    }

}
