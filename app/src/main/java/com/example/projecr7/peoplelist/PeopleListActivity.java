package com.example.projecr7.peoplelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PeopleListActivity extends AppCompatActivity {

    public static final String EXTRA_COUPLEID = "com.example.projecr7.COUPLEID";
    private RecyclerView peopleRecyclerView, coupleRecyclerView, familyRecyclerView;
    private RecyclerView.Adapter mPeopleAdapter, mCoupleAdapter, mFamilyAdapter;
    private RecyclerView.LayoutManager layoutManager, coupleLayoutManager, familyLayoutManager;
    private List<Person> people;
    private List<Couple> couples;
    private List<Family> families;
    private int dinnerId;

    private FloatingActionButton fab_main, fab1_person, fab2_couple, fab3_family;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_person, textview_couple, textview_family;
    Boolean isOpen = false;


    private onClickInterface mOnClickInterface, mCoupleOnClickInterface, mFamilyOnClickInterface;

    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);

        Intent intent = getIntent();
        if(intent.getIntExtra(MainActivity.EXTRA_INDEX, 0) != 0)
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
                Intent intent = new Intent(PeopleListActivity.this, ManagePersonActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, selectPeopleId);
                startActivity(intent);
            }
        };
        updatePeopleList();

        coupleRecyclerView = findViewById(R.id.my_recycler_view_couple_list);
        coupleRecyclerView.setHasFixedSize(true);
        coupleLayoutManager = new LinearLayoutManager(this);
        coupleRecyclerView.setLayoutManager(coupleLayoutManager);

        mCoupleOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Couple selectCouple = (Couple)i;
                int selectCoupleId = selectCouple.getUid();
                Intent intent = new Intent(PeopleListActivity.this, ManageCoupleActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(PeopleListActivity.EXTRA_COUPLEID, selectCoupleId);
                startActivity(intent);
            }
        };
        updateCoupleList();

        familyRecyclerView = findViewById(R.id.my_recycler_view_family_list);
        familyRecyclerView.setHasFixedSize(true);
        familyLayoutManager = new LinearLayoutManager(this);
        familyRecyclerView.setLayoutManager(familyLayoutManager);

        mFamilyOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Family selectFamily = (Family)i;
                int selectFamilyId = selectFamily.getUid();
                Intent intent = new Intent(PeopleListActivity.this, ManageFamilyActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(PeopleListActivity.EXTRA_COUPLEID, selectFamilyId);
                startActivity(intent);
            }
        };
        updateFamilyList();

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
        fab3_family = findViewById(R.id.fab_add_family);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_person = findViewById(R.id.textview_person);
        textview_couple = findViewById(R.id.textview_couple);
        textview_family = findViewById(R.id.textview_family);

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
                fabClose();
                Intent intent = new Intent(PeopleListActivity.this, AddCoupleActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

        fab3_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClose();
                Intent intent = new Intent(PeopleListActivity.this, AddFamilyActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

        fab1_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClose();
                Intent intent = new Intent(PeopleListActivity.this, AddPersonActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                startActivity(intent);
            }
        });

    }

    private void updateFamilyList() {
        families = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadAllByDinner(dinnerId);
        Family[] familyArray;
        if(families.size() > 0){
            familyArray =  new Family[families.size()];
            int j = 0;
            for(int i = 0; i < families.size(); i++){
                if(families.get(i).getUid() != 4) {
                    familyArray[j] = families.get(i);
                    j++;
                }
            }
            mFamilyAdapter = new MyFamilyListAdapter(familyArray, mFamilyOnClickInterface);
            familyRecyclerView.setAdapter(mFamilyAdapter);
        }
    }

    private void updateCoupleList() {
        couples = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().coupleDao().loadAllByDinner(dinnerId);
        Couple[] coupleArray;
        if(couples.size() > 0){
            coupleArray =  new Couple[couples.size()];
            int j = 0;
            for(int i = 0; i < couples.size(); i++){
                if(couples.get(i).getUid() != 4) {
                    coupleArray[j] = couples.get(i);
                    j++;
                }
            }
            mCoupleAdapter = new MyCoupleListAdapter(coupleArray, mCoupleOnClickInterface);
            coupleRecyclerView.setAdapter(mCoupleAdapter);
        }
    }

    public void updatePeopleList(){
        people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
 //       people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().getAll();
        ArrayList<Person> singlePeople = new ArrayList<Person>();
        for(int i = 0; i < people.size(); i++){
            if(people.get(i).getCoupleId() == 4 && people.get(i).getFamilyId() == 4 )
                singlePeople.add(people.get(i));
        }
        Person[] peopleArray;
        if(singlePeople.size() != 0){
            peopleArray =  new Person[singlePeople.size()];
            for(int i = 0; i < singlePeople.size(); i++){
                peopleArray[i] = singlePeople.get(i);
            }
            mPeopleAdapter = new MyPeopleListAdapter(peopleArray, mOnClickInterface);
            peopleRecyclerView.setAdapter(mPeopleAdapter);
        }
    }

    private void fabOpen(){
        textview_person.setVisibility(View.VISIBLE);
        textview_couple.setVisibility(View.VISIBLE);
        textview_family.setVisibility(View.VISIBLE);
        fab3_family.startAnimation(fab_open);
        fab2_couple.startAnimation(fab_open);
        fab1_person.startAnimation(fab_open);
        fab_main.startAnimation(fab_clock);
        fab3_family.setClickable(true);
        fab2_couple.setClickable(true);
        fab1_person.setClickable(true);
        isOpen = true;
    }

    private void fabClose(){
        textview_person.setVisibility(View.INVISIBLE);
        textview_couple.setVisibility(View.INVISIBLE);
        textview_family.setVisibility(View.INVISIBLE);
        fab3_family.startAnimation(fab_close);
        fab2_couple.startAnimation(fab_close);
        fab1_person.startAnimation(fab_close);
        fab_main.startAnimation(fab_anticlock);
        fab3_family.setClickable(false);
        fab2_couple.setClickable(false);
        fab1_person.setClickable(false);
        isOpen = false;
    }
}
