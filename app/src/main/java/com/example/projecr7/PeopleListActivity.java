package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PeopleListActivity extends AppCompatActivity {

    private RecyclerView peopleRecyclerView;
    private RecyclerView.Adapter mPeopleAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Person> people;
    private int dinnerId;

    private FloatingActionButton fab_main, fab1_person, fab2_couple;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_person, textview_couple;
    Boolean isOpen = false;


    private onClickInterface mOnClickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        peopleRecyclerView = findViewById(R.id.my_recycler_view_people_list);
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

//        FloatingActionButton fab = findViewById(R.id.fab_people);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(PeopleListActivity.this,AddPersonActivity.class);
//                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
//                startActivity(intent);
//            }
//        });

        fab_main = findViewById(R.id.fab_main_people);
        fab1_person = findViewById(R.id.fab_people);
        fab2_couple = findViewById(R.id.fab_add_couple);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_person = findViewById(R.id.textview_person);
        textview_couple = findViewById(R.id.textview_couple);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fabClose();
                } else {
                    fabOpen();
                }

            }
        });


        fab2_couple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        fab1_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClose();
                Intent intent = new Intent(PeopleListActivity.this,AddPersonActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

    }

    public void updatePeopleList(){
        people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Person[] peopleArray;
        if(people.size() != 0){
            peopleArray =  new Person[people.size()];
            for(int i = 0; i < people.size(); i++){
                peopleArray[i] = people.get(i);
            }
            mPeopleAdapter = new MyPeopleListAdapter(peopleArray, mOnClickInterface);
            peopleRecyclerView.setAdapter(mPeopleAdapter);
        }
    }

    private void fabOpen(){
        textview_person.setVisibility(View.VISIBLE);
        textview_couple.setVisibility(View.VISIBLE);
        fab2_couple.startAnimation(fab_open);
        fab1_person.startAnimation(fab_open);
        fab_main.startAnimation(fab_clock);
        fab2_couple.setClickable(true);
        fab1_person.setClickable(true);
        isOpen = true;
    }

    private void fabClose(){
        textview_person.setVisibility(View.INVISIBLE);
        textview_couple.setVisibility(View.INVISIBLE);
        fab2_couple.startAnimation(fab_close);
        fab1_person.startAnimation(fab_close);
        fab_main.startAnimation(fab_anticlock);
        fab2_couple.setClickable(false);
        fab1_person.setClickable(false);
        isOpen = false;
    }
}
