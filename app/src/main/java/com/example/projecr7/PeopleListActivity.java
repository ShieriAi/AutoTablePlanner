package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.widget.TextView;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class PeopleListActivity extends AppCompatActivity {

    private RecyclerView peopleRecyclerView;
    private RecyclerView.Adapter mPeopleAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Person> people;
    private int dinnerId;

    private onClickInterface mOnClickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        peopleRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_people_list);
        peopleRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        peopleRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Person selectPerson = (Person)i;
                int selectPeopleId = selectPerson.getId();
                Intent intent = new Intent(PeopleListActivity.this,ManagePersonActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, selectPeopleId);
                startActivity(intent);
            }
        };
        updatePeopleList();

        TextView textview = findViewById(R.id.textViewDinnerName_peoplelist);
        textview.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId).toString());

        FloatingActionButton fab = findViewById(R.id.fab_people);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PeopleListActivity.this,AddPersonActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

    }

    public void updatePeopleList(){
        people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Person[] peopleArray;;
        if(people.size() != 0){
            peopleArray =  new Person[people.size()];
            for(int i = 0; i < people.size(); i++){
                peopleArray[i] = people.get(i);
            }
            mPeopleAdapter = new MyPeopleListAdapter(peopleArray, mOnClickInterface);
            peopleRecyclerView.setAdapter(mPeopleAdapter);
        }
    }
}
