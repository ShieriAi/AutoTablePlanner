package com.example.projecr7.bribes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;

import java.util.ArrayList;
import java.util.List;

public class AddBribeActivity extends AppCompatActivity {

    private int dinnerId;
    private Spinner typeSpinner, guestSpinner;
    EditText amountInput;

    private String[] guestArraySpinner;
    private ArrayList<Integer> guestIdArray;

    private int satis;
    private TextView seekbar_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bribe);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);

        Button addBribeBtn = findViewById(R.id.button_add_bribe_with_name);
        typeSpinner = findViewById(R.id.bribe_select_type_spinner);
        guestSpinner = findViewById(R.id.bribe_select_guest_spinner);
        amountInput = findViewById(R.id.bribe_amount_input);

        String[] typeArraySpinner = new String[3];
        typeArraySpinner[0] = "single";
        typeArraySpinner[1] = "couple";
        typeArraySpinner[2] = "family";

        ArrayAdapter<String> typeSAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, typeArraySpinner);
        typeSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeSAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        updateGuestListSingle();
                        break;
                    case 1:
                        List<Couple> coupleList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().loadAllByDinner(dinnerId);
                        guestIdArray = new ArrayList<>();
                        guestArraySpinner = new String[coupleList.size()];
                        for(int i = 0; i < coupleList.size(); i++){
                            guestIdArray.add(coupleList.get(i).getUid());
                            guestArraySpinner[i] = coupleList.get(i).getDisplayName();
                        }
                        break;
                    case 2:
                        List<Family> familyList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadAllByDinner(dinnerId);
                        guestIdArray = new ArrayList<>();
                        guestArraySpinner = new String[familyList.size()];
                        for(int i = 0; i < familyList.size(); i++){
                            guestIdArray.add(familyList.get(i).getUid());
                            guestArraySpinner[i] = familyList.get(i).getDisplayName();
                        }
                        break;
                }
                updateGuestSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        SeekBar satis_seekbar = findViewById(R.id.seekBar_add_bribe);
        seekbar_display = findViewById(R.id.bribe_satis_seekbar_display);
        satis = 0;
        satis_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                satis = progress;
                seekbar_display.setText(Integer.toString(satis));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        addBribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int guestId = guestIdArray.get(guestSpinner.getSelectedItemPosition());
                int guestType = typeSpinner.getSelectedItemPosition() + 1;
                Bribe newBribe = new Bribe(dinnerId, guestId, guestType, guestSpinner.getSelectedItem().toString(), Integer.parseInt(amountInput.getText().toString()));
                newBribe.setSatis(satis);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().bribeDao().insert(newBribe);
                Intent intent = new Intent(AddBribeActivity.this, BribeListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

    }

    private void updateGuestListSingle(){
        List<Person> personList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        ArrayList<Person> list = new ArrayList<Person>();
        guestIdArray = new ArrayList<Integer>();
        for(int i = 0; i < personList.size(); i++){
            if(personList.get(i).getCoupleId() == 4 && personList.get(i).getFamilyId() == 4){
                guestIdArray.add(personList.get(i).getId());
                list.add(personList.get(i));
            }
        }
        guestArraySpinner = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            guestArraySpinner[i] = list.get(i).getName();
        }
    }

    private void updateGuestSpinner() {
        ArrayAdapter<String> guestSAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, guestArraySpinner);
        guestSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guestSpinner.setAdapter(guestSAdapter);
    }
}
