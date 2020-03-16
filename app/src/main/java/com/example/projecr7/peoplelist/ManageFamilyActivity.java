package com.example.projecr7.peoplelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;

import java.util.List;

public class ManageFamilyActivity extends AppCompatActivity {

    private int dinnerId;
    private long familyId;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] enteredStrings;
    private String[] enteredGenders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_family);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        familyId = intent.getLongExtra(PeopleListActivity.EXTRA_COUPLEID, 0);

        mRecyclerView = findViewById(R.id.my_recycler_view_edit_family_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Family currentF = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadSingleById(familyId);
        final List<Person> currentP = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByFamily(familyId);

        enteredGenders = new String[currentF.getFamilySize()];
        enteredStrings = new String[currentF.getFamilySize()];
        for(int i = 0; i < currentF.getFamilySize(); i++){
            enteredStrings[i] = currentP.get(i).getName();
            enteredGenders[i] = currentP.get(i).getGender();
        }

        mAdapter = new MyManageFamilyAdapter(currentP, new OnEditTextChanged() {
            @Override
            public void onTextChanged(int position, String name) {
                enteredStrings[position] = name;
            }
        }, new OnSpinnerChanged() {
            @Override
            public void onSelectChanged(int position, String gender) {
                enteredGenders[position] = gender;
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        Button editFamilyBtn = findViewById(R.id.button_save_family);
        Button deleteFamilyBtn = findViewById(R.id.button_delete_family);

        editFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < currentF.getFamilySize(); i++) {
                    if (enteredStrings[i].matches("")) {
                        return;
                    }
                }
                for(int i = 0; i < currentF.getFamilySize(); i++) {
                    if (!enteredStrings[i].matches(currentP.get(i).getName()) || !enteredGenders[i].matches(currentP.get(i).getGender())) {
                        Person newPerson = new Person(enteredStrings[i], enteredGenders[i]);
                        newPerson.setId(currentP.get(i).getId());
                        newPerson.setDinnerId(currentP.get(i).getDinnerId());
                        newPerson.setTableId(currentP.get(i).getTableId());
                        newPerson.setSeatId(currentP.get(i).getSeatId());
                        newPerson.setFamilyId(familyId);
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().updateUsers(newPerson);
                    }
                }
                Intent intent = new Intent(ManageFamilyActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        deleteFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageFamilyActivity.this, PeopleListActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().delete(currentF);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
