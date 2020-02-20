package com.example.projecr7.CalculateTableAlgorithm;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.database.Table;

import java.util.List;

public class MainAlgorithm {

    private int dinnerId;
    private int dinnerSize;
    private List<Person> personList;
    private List<Couple> coupleList;
    private List<Family> familyList;
    private List<Proximity> proximityList;
    private List<Table> tableList;
    private List<Bribe> bribeList;

    public MainAlgorithm(int dinnerId){
        this.dinnerId = dinnerId;
        personList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        coupleList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().coupleDao().loadAllByDinner(dinnerId);
        familyList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().familyDao().loadAllByDinner(dinnerId);
        proximityList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().proximityDao().loadAllByDinner(dinnerId);
        tableList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        bribeList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().bribeDao().loadAllByDinner(dinnerId);
        dinnerSize = 0;
        for(int i = 0; i < tableList.size(); i++){
            dinnerSize += tableList.get(i).getTableSize();
        }
    }

    // check if it's too much people
    public boolean checkDinnerSize(){
        if(dinnerSize < personList.size())
            return false;
        else
            return true;
    }

    public void randomSeat(){
        int numberOfPeopleInSeat = 0;
        int currentTableId;
        for(int i = 0; i < tableList.size(); i++){
            currentTableId = tableList.get(i).getUid();
            for(int j = 0; j < tableList.get(i).getTableSize(); j++){
                Person currentPerson = personList.get(numberOfPeopleInSeat);
                currentPerson.setTableId(currentTableId);
                currentPerson.setSeatId(j);
                DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().updateUsers(currentPerson);
                numberOfPeopleInSeat++;
                if(numberOfPeopleInSeat == personList.size())
                    break;
            }
        }
    }

}
