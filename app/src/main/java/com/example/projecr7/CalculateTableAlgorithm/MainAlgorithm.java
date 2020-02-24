package com.example.projecr7.CalculateTableAlgorithm;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.database.Table;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Single;

class SortbyDislikeF implements Comparator<Family>
{
    @Override
    public int compare(Family o1, Family o2) {
        return o2.getDisLikeBy() - o1.getDisLikeBy();
    }
}

class SortbyDislikeC implements Comparator<Couple>
{
    @Override
    public int compare(Couple o1, Couple o2) {
        return o2.getDisLikeBy() - o1.getDisLikeBy();
    }
}

class SortbyDislikeS implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return o2.getDisLikeBy() - o1.getDisLikeBy();
    }
}

class SortbyTableSize implements Comparator<Table>
{
    @Override
    public int compare(Table o1, Table o2) {
        return o1.getTableSize() - o2.getTableSize();
    }
}

public class MainAlgorithm {

    private int dinnerId;
    private int dinnerSize;
    private List<Person> personList, singleList;
    private List<Couple> coupleList;
    private List<Family> familyList;
    private List<Proximity> proximityList;
    private List<Table> tableList;
    private List<Bribe> bribeList;

    public MainAlgorithm(int dinnerId){
        this.dinnerId = dinnerId;
        personList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        singleList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllSingles(dinnerId);
        Collections.sort(singleList, new SortbyDislikeS());
        coupleList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().coupleDao().loadAllByDinner(dinnerId);
        Collections.sort(coupleList, new SortbyDislikeC());
        familyList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().familyDao().loadAllByDinner(dinnerId);
        Collections.sort(familyList, new SortbyDislikeF());
        proximityList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().proximityDao().loadAllByDinner(dinnerId);
        tableList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Collections.sort(tableList, new SortbyTableSize());
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

    public void tryBest(){
        int numberOfGuest = singleList.size()+coupleList.size()+familyList.size();
        int[][] guest = new int[numberOfGuest][5];
        int guestIndex = 0;
        for(int i = 0; i < familyList.size(); i++){
            guest[guestIndex][0] = familyList.get(i).getUid();
            guest[guestIndex][1] = 3;
            guest[guestIndex][2] = familyList.get(i).getFamilySize();
            guest[guestIndex][3] = 4;
            guest[guestIndex][4] = -1;
            guestIndex++;
        }
        for(int i = 0; i < coupleList.size(); i++){
            guest[guestIndex][0] = coupleList.get(i).getUid();
            guest[guestIndex][1] = 2;
            guest[guestIndex][2] = 2;
            guest[guestIndex][3] = 4;
            guest[guestIndex][4] = -1;
            guestIndex++;
        }
        for(int i = 0; i < singleList.size(); i++){
            guest[guestIndex][0] = singleList.get(i).getId();
            guest[guestIndex][1] = 1;
            guest[guestIndex][2] = 1;
            guest[guestIndex][3] = 4;
            guest[guestIndex][4] = -1;
            guestIndex++;
        }

        LinkedList<Integer> edges[] = new LinkedList[numberOfGuest];
        int current1P = -1, current2P = -1;
        boolean found = false;
        for(int i = 0; i < proximityList.size(); i++){
            current1P = -1; current2P = -1;
            if(proximityList.get(i).getProximityType() == 1){
                switch (proximityList.get(i).getType1()){
                    case 1:
                        for(int j = familyList.size()+coupleList.size(); j < guestIndex; j++){
                            if(guest[j][0] == proximityList.get(i).guest1Id){
                                current1P = j;
                                if(found){
                                    found = false;
                                    break;
                                }
                                else  found = true;
                            }
                            else if(guest[j][0] == proximityList.get(i).guest2Id){
                                current2P = j;
                                if(found){
                                    found = false;
                                    break;
                                }
                                else  found = true;
                            }
                        }
                        break;
                    case 2:
                        for(int j = familyList.size(); j < familyList.size()+coupleList.size(); j++){
                            if(guest[j][0] == proximityList.get(i).guest1Id){
                                current1P = j;
                                if(found){
                                    found = false;
                                    break;
                                }
                                else  found = true;
                            }
                            else if(guest[j][0] == proximityList.get(i).guest2Id){
                                current2P = j;
                                if(found){
                                    found = false;
                                    break;
                                }
                                else  found = true;
                            }
                        }
                        break;
                    case 3:
                        for(int j = 0; j < familyList.size(); j++){
                            if(guest[j][0] == proximityList.get(i).guest1Id){
                                current1P = j;
                                if(found){
                                    found = false;
                                    break;
                                }
                                else  found = true;
                            }
                            else if(guest[j][0] == proximityList.get(i).guest2Id){
                                current2P = j;
                                if(found){
                                    found = false;
                                    break;
                                }
                                else  found = true;
                            }
                        }
                        break;
                } // end switch
                if(current1P != -1 && current2P != -1) {
                    edges[current1P].add(current2P);
                    edges[current2P].add(current1P);
                }
            }
        } // end for

        kColorable tryK = new kColorable(guestIndex, guest, edges, tableList);
        if(tryK.tryGraphColoring(0)){

        }
        else
            tryK.insertLeftGuest();
    } // end tryBest



}
