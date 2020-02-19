package com.example.projecr7.proximity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Proximity;

public class ManageSingleProximityActivity extends AppCompatActivity {

    private Proximity currentProximity;
    private int dinnerId, guestId, guestType, proximityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_single_proximity);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);
        guestId = intent.getIntExtra(ManageProximityActivity.EXTRA_GUESTID, 4);
        guestType = intent.getIntExtra(ManageProximityActivity.EXTRA_GUESTTYPE, 4);
        proximityId = intent.getIntExtra(proximityListActivity.EXTRA_ID, 4);

        Button deleteBtn = findViewById(R.id.button_delete_proximity);
        TextView guest1Textview = findViewById(R.id.proximity_manage_item_me_textview);
        TextView guest2Textview = findViewById(R.id.proximity_manage_item_other_textview);
        ImageView icon = findViewById(R.id.proximity_manage_item_imageview);

        currentProximity = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().proximityDao().loadSingleById(proximityId);
        switch (currentProximity.getProximityType()){
            case 1:
                icon.setImageResource(R.drawable.sad_icon);
                break;
            case 2:
                icon.setImageResource(R.drawable.uphappy_icon);
                break;
            case 4:
                icon.setImageResource(R.drawable.happy_icon);
                break;
            case 5:
                icon.setImageResource(R.drawable.happyplus_icon);
                break;
        }
        guest1Textview.setText(currentProximity.getGuest1String());
        guest2Textview.setText(currentProximity.getGuest2String());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().proximityDao().delete(currentProximity);
                Intent intent = new Intent(ManageSingleProximityActivity.this, ManageProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(proximityListActivity.EXTRA_TYPE, guestType);
                intent.putExtra(proximityListActivity.EXTRA_ID, guestId);
                startActivity(intent);
            }
        });

    }
}
