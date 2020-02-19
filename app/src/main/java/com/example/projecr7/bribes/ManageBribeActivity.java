package com.example.projecr7.bribes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;

public class ManageBribeActivity extends AppCompatActivity {

    private int dinnerId, bribeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bribe);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);
        bribeId = intent.getIntExtra(BribeListActivity.EXTRA_BRIBEID, 4);
    }
}
