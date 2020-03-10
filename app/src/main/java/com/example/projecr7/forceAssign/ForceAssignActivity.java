package com.example.projecr7.forceAssign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.projecr7.MainActivity;
import com.example.projecr7.R;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.onClickInterface;
import com.example.projecr7.peoplelist.MyPeopleListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SortbyFamily implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return o1.getFamilyId() - o2.getFamilyId();
    }
}
class SortbyCouple implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return o1.getCoupleId() - o2.getCoupleId();
    }
}

public class ForceAssignActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.projecr7.typeid";
    private RecyclerView peopleRecyclerView, coupleRecyclerView, familyRecyclerView;
    private RecyclerView.Adapter mPeopleAdapter, mCoupleAdapter, mFamilyAdapter;
    private RecyclerView.LayoutManager layoutManager, coupleLayoutManager, familyLayoutManager;
    private List<Person> people;
    private int dinnerId;

    private onClickInterface mOnClickInterface;

    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_assign);

        Intent intent = getIntent();
        dinnerId = intent.getIntExtra(MainActivity.EXTRA_INDEX, 4);

        peopleRecyclerView = findViewById(R.id.my_recycler_view_people_list);
        peopleRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        peopleRecyclerView.setLayoutManager(layoutManager);

        mOnClickInterface = new onClickInterface() {
            @Override
            public void setClick(Object i) {
                Person selectPerson = (Person) i;
                int selectPeopleId = selectPerson.getId();
                Intent intent = new Intent(com.example.projecr7.forceAssign.ForceAssignActivity.this, ManageSinglePersonTableActivity.class);
                intent.putExtra(MainActivity.EXTRA_INDEX, dinnerId);
                intent.putExtra(ForceAssignActivity.EXTRA_ID, selectPeopleId);
                startActivityForResult(intent,1);
            }
        };

        coupleRecyclerView = findViewById(R.id.my_recycler_view_couple_list);
        coupleRecyclerView.setHasFixedSize(true);
        coupleLayoutManager = new LinearLayoutManager(this);
        coupleRecyclerView.setLayoutManager(coupleLayoutManager);

        familyRecyclerView = findViewById(R.id.my_recycler_view_family_list);
        familyRecyclerView.setHasFixedSize(true);
        familyLayoutManager = new LinearLayoutManager(this);
        familyRecyclerView.setLayoutManager(familyLayoutManager);

        updatePeopleList();

        TextView textview1 = findViewById(R.id.textViewDinnerName_forceAssign1);
        textview1.setText(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId).toString());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode ==  RESULT_OK){
                dinnerId = data.getIntExtra(MainActivity.EXTRA_INDEX, 4);
                updatePeopleList();
            }
        }
    }

    public void updatePeopleList() {
        people = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Person[] peopleArray;
        Person[] coupleA;
        Person[] familyA;
        ArrayList<Person> singleArray = new ArrayList<Person>();
        ArrayList<Person> coupleArray = new ArrayList<Person>();
        ArrayList<Person> familyArray = new ArrayList<Person>();
        if (people.size() != 0) {
            for(int i = 0; i < people.size(); i++){
                if(people.get(i).getFamilyId() != 4)
                    familyArray.add(people.get(i));
                else if(people.get(i).getCoupleId() != 4)
                    coupleArray.add(people.get(i));
                else
                    singleArray.add(people.get(i));
            }
            Collections.sort(coupleArray, new SortbyCouple());
            Collections.sort(familyArray, new SortbyFamily());
            peopleArray = new Person[singleArray.size()];
            coupleA = new Person[coupleArray.size()];
            familyA = new Person[familyArray.size()];
            for(int i = 0; i < familyArray.size(); i++){
                familyA[i] = familyArray.get(i);
            }
            for(int i = 0; i < coupleArray.size(); i++){
                coupleA[i] = coupleArray.get(i);
            }
            for(int i = 0; i < singleArray.size(); i++){
                peopleArray[i] = singleArray.get(i);
            }
            mPeopleAdapter = new MyPeopleListAdapter(peopleArray, mOnClickInterface);
            peopleRecyclerView.setAdapter(mPeopleAdapter);
            mCoupleAdapter = new MyPeopleListAdapter(coupleA, mOnClickInterface);
            coupleRecyclerView.setAdapter(mCoupleAdapter);
            mFamilyAdapter = new MyPeopleListAdapter(familyA, mOnClickInterface);
            familyRecyclerView.setAdapter(mFamilyAdapter);
        }
    }
}
