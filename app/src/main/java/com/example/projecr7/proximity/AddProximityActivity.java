package com.example.projecr7.proximity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.peoplelist.AddFamilyActivity;
import com.example.projecr7.peoplelist.PeopleListActivity;

import java.util.ArrayList;
import java.util.List;

public class AddProximityActivity extends AppCompatActivity {

    private int dinnerId;
    private int guestId;
    private int guestType;

    private ImageView icon;
    private TextView nameDisplay;
    private Spinner typeSpinner, guestSpinner, proximityTypeSpinner;

    private String[] guestArraySpinner;
    private String guestName;
    private ArrayList<Integer> guestIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_proximity);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);
        guestId = intent.getIntExtra(ManageProximityActivity.EXTRA_GUESTID, 4);
        guestType = intent.getIntExtra(ManageProximityActivity.EXTRA_GUESTTYPE, 4);

        icon = findViewById(R.id.proximity_guest1_name_icon_display);
        nameDisplay = findViewById(R.id.proximity_guest1_name_display);
        typeSpinner = findViewById(R.id.proximity_select_type_spinner);
        guestSpinner = findViewById(R.id.proximity_select_guest_spinner);
        proximityTypeSpinner = findViewById(R.id.proximity_select_proximity_spinner);

        switch (guestType){
            case 1:
                Person currentPerson = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadSingleById(guestId);
                guestName = currentPerson.getName();
                break;
            case 2:
                Couple currentCouple = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().loadSingleById(guestId);
                guestName = currentCouple.getDisplayName();
                break;
            case 3:
                Family currentFamily = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadSingleById(guestId);
                guestName = currentFamily.getDisplayName();
                break;
        }

        guestIdArray = new ArrayList<Integer>();

        String[] typeArraySpinner = new String[3];
        typeArraySpinner[0] = "single";
        typeArraySpinner[1] = "couple";
        typeArraySpinner[2] = "family";

        ArrayAdapter<String> typeSAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, typeArraySpinner);
        typeSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeSAdapter);

        String[] proximityArraySpinner = new String[4];
        proximityArraySpinner[0] = "far away";
        proximityArraySpinner[1] = "not near to";
        proximityArraySpinner[2] = "near to";
        proximityArraySpinner[3] = "next to";

        ArrayAdapter<String> proximitySAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, proximityArraySpinner);
        proximitySAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proximityTypeSpinner.setAdapter(proximitySAdapter);

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
                        if(guestType == 2){
                            guestArraySpinner = new String[coupleList.size() - 1];
                            int j = 0;
                            for(int i = 0; i < coupleList.size(); i++){
                                if(coupleList.get(i).getUid() != guestId) {
                                    guestIdArray.add(coupleList.get(i).getUid());
                                    guestArraySpinner[j] = coupleList.get(i).getDisplayName();
                                    j++;
                                }
                            }
                        }
                        else{
                            guestArraySpinner = new String[coupleList.size()];
                            for(int i = 0; i < coupleList.size(); i++){
                                guestIdArray.add(coupleList.get(i).getUid());
                                guestArraySpinner[i] = coupleList.get(i).getDisplayName();
                            }
                        }
                        break;
                    case 2:
                        List<Family> familyList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadAllByDinner(dinnerId);
                        guestIdArray = new ArrayList<>();
                        if(guestType == 3){
                            guestArraySpinner = new String[familyList.size() - 1];
                            int j = 0;
                            for(int i = 0; i < familyList.size(); i++){
                                if(familyList.get(i).getUid() != guestId) {
                                    guestIdArray.add(familyList.get(i).getUid());
                                    guestArraySpinner[j] = familyList.get(i).getDisplayName();
                                    j++;
                                }
                            }
                        }
                        else{
                            guestArraySpinner = new String[familyList.size()];
                            for(int i = 0; i < familyList.size(); i++){
                                guestIdArray.add(familyList.get(i).getUid());
                                guestArraySpinner[i] = familyList.get(i).getDisplayName();
                            }
                        }
                        break;
                }
                updateGuestSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateGuestListSingle();

        updateGuestSpinner();

        nameDisplay.setText(guestName);

        Button addProximityBtn = findViewById(R.id.button_add_proximity_with_name);
        addProximityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int proximityType = proximityTypeSpinner.getSelectedItemPosition() + 1;
                if(proximityType >= 3){
                    proximityType++;
                }
                int guest2Id = guestIdArray.get(guestSpinner.getSelectedItemPosition());
                List<Proximity> proximityList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().proximityDao().loadAllByGuest1(guestId);
                boolean found = false;
                int foundIndex = 0;
                for(int i = 0; i < proximityList.size(); i++){
                    if(guest2Id == proximityList.get(i).getGuest2Id()){
                        found = true;
                        foundIndex = i;
                        continue;
                    }
                }
                if(!found) {
                    Proximity newProximity = new Proximity(dinnerId, guestType, typeSpinner.getSelectedItemPosition() + 1, guestId, guest2Id, proximityType);
                    newProximity.setGuest1String(guestName);
                    newProximity.setGuest2String(guestSpinner.getSelectedItem().toString());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().proximityDao().insert(newProximity);
                }
                else{
                    Proximity newProximity = new Proximity(dinnerId, guestType, typeSpinner.getSelectedItemPosition() + 1, guestId, guest2Id, proximityType);
                    newProximity.setGuest1String(guestName);
                    newProximity.setGuest2String(guestSpinner.getSelectedItem().toString());
                    newProximity.setUid(proximityList.get(foundIndex).getUid());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().proximityDao().updateUsers(newProximity);
                }
                Intent intent = new Intent(AddProximityActivity.this, ManageProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(proximityListActivity.EXTRA_TYPE, guestType);
                intent.putExtra(proximityListActivity.EXTRA_ID, guestId);
                startActivity(intent);
            }
        });

        icon = findViewById(R.id.proximity_guest1_name_icon_display);
        switch (guestType){
            case 1:
                icon.setImageResource(R.drawable.single_icon);
                break;
            case 2:
                icon.setImageResource(R.drawable.couple_icon);
                break;
            case 3:
                icon.setImageResource(R.drawable.family_icon);
                break;
        }


    }

    private void updateGuestListSingle(){
        List<Person> personList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        ArrayList<Person> list = new ArrayList<Person>();
        guestIdArray = new ArrayList<Integer>();
        for(int i = 0; i < personList.size(); i++){
            if(personList.get(i).getCoupleId() == 4 && personList.get(i).getFamilyId() == 4 && personList.get(i).getId() != guestId){
                guestIdArray.add(personList.get(i).getId());
                list.add(personList.get(i));
            }
        }
        guestArraySpinner = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            guestArraySpinner[i] = list.get(i).getName();
        }
    }

    private void updateGuestSpinner(){
        ArrayAdapter<String> guestSAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, guestArraySpinner);
        guestSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guestSpinner.setAdapter(guestSAdapter);
    }

}
