package com.example.projecr7.proximity;

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
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;
import com.example.projecr7.peoplelist.AddCoupleActivity;
import com.example.projecr7.peoplelist.AddFamilyActivity;
import com.example.projecr7.peoplelist.AddPersonActivity;
import com.example.projecr7.peoplelist.ManageCoupleActivity;
import com.example.projecr7.peoplelist.ManageFamilyActivity;
import com.example.projecr7.peoplelist.ManagePersonActivity;
import com.example.projecr7.peoplelist.MyCoupleListAdapter;
import com.example.projecr7.peoplelist.MyFamilyListAdapter;
import com.example.projecr7.peoplelist.MyPeopleListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class proximityListActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "com.example.projecr7.type";
    public static final String EXTRA_ID = "com.example.projecr7.typeid";
    private RecyclerView peopleRecyclerView, coupleRecyclerView, familyRecyclerView;
    private RecyclerView.Adapter mPeopleAdapter, mCoupleAdapter, mFamilyAdapter;
    private RecyclerView.LayoutManager layoutManager, coupleLayoutManager, familyLayoutManager;
    private List<Person> people;
    private List<Couple> couples;
    private List<Family> families;
    private int dinnerId;

    private onClickInterface mOnClickInterface, mCoupleOnClickInterface, mFamilyOnClickInterface;

    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_list);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);

        peopleRecyclerView = findViewById(R.id.my_recycler_view_people_list);
        peopleRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        peopleRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Person selectPerson = (Person) i;
                int selectPeopleId = selectPerson.getId();
                Intent intent = new Intent(com.example.projecr7.proximity.proximityListActivity.this, ManageProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(proximityListActivity.EXTRA_TYPE, 1);
                intent.putExtra(proximityListActivity.EXTRA_ID, selectPeopleId);
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
                Couple selectCouple = (Couple) i;
                int selectCoupleId = selectCouple.getUid();
                Intent intent = new Intent(com.example.projecr7.proximity.proximityListActivity.this, ManageProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(proximityListActivity.EXTRA_TYPE, 2);
                intent.putExtra(proximityListActivity.EXTRA_ID, selectCoupleId);
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
                Family selectFamily = (Family) i;
                int selectFamilyId = selectFamily.getUid();
                Intent intent = new Intent(com.example.projecr7.proximity.proximityListActivity.this, ManageProximityActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(proximityListActivity.EXTRA_TYPE, 3);
                intent.putExtra(proximityListActivity.EXTRA_ID, selectFamilyId);
                startActivity(intent);
            }
        };
        updateFamilyList();

        TextView textview = findViewById(R.id.textViewDinnerName_proximityList);
        textview.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId).toString());

    }

    private void updateFamilyList() {
        families = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().familyDao().loadAllByDinner(dinnerId);
        Family[] familyArray;
        if (families.size() > 0) {
            familyArray = new Family[families.size()];
            int j = 0;
            for (int i = 0; i < families.size(); i++) {
                if (families.get(i).getUid() != 4) {
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
        if (couples.size() > 0) {
            coupleArray = new Couple[couples.size()];
            int j = 0;
            for (int i = 0; i < couples.size(); i++) {
                if (couples.get(i).getUid() != 4) {
                    coupleArray[j] = couples.get(i);
                    j++;
                }
            }
            mCoupleAdapter = new MyCoupleListAdapter(coupleArray, mCoupleOnClickInterface);
            coupleRecyclerView.setAdapter(mCoupleAdapter);
        }
    }

    public void updatePeopleList() {
        people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        ArrayList<Person> singlePeople = new ArrayList<Person>();
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getCoupleId() == 4 && people.get(i).getFamilyId() == 4)
                singlePeople.add(people.get(i));
        }
        Person[] peopleArray;
        if (singlePeople.size() != 0) {
            peopleArray = new Person[singlePeople.size()];
            for (int i = 0; i < singlePeople.size(); i++) {
                peopleArray[i] = singlePeople.get(i);
            }
            mPeopleAdapter = new MyPeopleListAdapter(peopleArray, mOnClickInterface);
            peopleRecyclerView.setAdapter(mPeopleAdapter);
        }
    }
}
