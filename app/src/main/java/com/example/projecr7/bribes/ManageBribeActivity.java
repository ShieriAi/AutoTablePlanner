package com.example.projecr7.bribes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.DatabaseClient;

public class ManageBribeActivity extends AppCompatActivity {

    private int dinnerId, bribeId;
    private Bribe currentBribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bribe);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);
        bribeId = intent.getIntExtra(BribeListActivity.EXTRA_BRIBEID, 4);

        Button saveBtn = findViewById(R.id.button_edit_bribe);
        Button deleteBtn = findViewById(R.id.button_delete_bribe);
        TextView nameDisplay = findViewById(R.id.bribe_item_name_textview_manage);
        final EditText amountInput = findViewById(R.id.bribe_item_amount_edit);

        currentBribe = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().bribeDao().loadSingleById(bribeId);
        nameDisplay.setText(currentBribe.getGuestName());
        amountInput.setText(Integer.toString(currentBribe.getBribeAmount()), TextView.BufferType.EDITABLE);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().bribeDao().delete(currentBribe);
                Intent intent = new Intent(ManageBribeActivity.this, BribeListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(amountInput.getText().toString());
                if(amount != currentBribe.getBribeAmount()){
                    Bribe newBribe = new Bribe(dinnerId, currentBribe.getGuestId(), currentBribe.getGuestType(), currentBribe.getGuestName(), amount);
                    newBribe.setUid(bribeId);
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().bribeDao().updateUsers(newBribe);
                }
                Intent intent = new Intent(ManageBribeActivity.this, BribeListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });
    }
}
