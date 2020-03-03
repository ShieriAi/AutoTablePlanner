package com.example.projecr7.CalculateTableAlgorithm;

import android.util.Log;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.database.Table;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

//class SortbyDislikeF implements Comparator<Family>
//{
//    @Override
//    public int compare(Family o1, Family o2) {
//        return o2.getDisLikeBy() - o1.getDisLikeBy();
//    }
//}
//
//class SortbyDislikeC implements Comparator<Couple>
//{
//    @Override
//    public int compare(Couple o1, Couple o2) {
//        return o2.getDisLikeBy() - o1.getDisLikeBy();
//    }
//}

class SortbyDislikeS implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return o2.getDisLikeBy() - o1.getDisLikeBy();
    }
}

class SortbyCouple implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return o2.getCoupleId() - o1.getCoupleId();
    }
}

class SortbyFamily implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return o2.getFamilyId() - o1.getFamilyId();
    }
}

class SortbyTableSize implements Comparator<Table>
{
    @Override
    public int compare(Table o1, Table o2) {
        return o1.getTableSize() - o2.getTableSize();
    }
}

class SortbyBribeSatis implements Comparator<Bribe>
{
    @Override
    public int compare(Bribe o1, Bribe o2) {
        return o2.getSatis() - o1.getSatis();
    }
}

class Guest{
    int id, table, seat, happiness;
    Boolean lock;
    LinkedList<Guest> loves;
    LinkedList<Guest> hates;
}

class TableAlgo{
    int id, seatLeft, tableSize;
    int[] seats;
    Boolean[] lock;
}

public class MainAlgorithm {

    private int dinnerId;
    private int dinnerSize, numberOfGuest;
    private List<Person> personList;
    private List<Person> coupleList; // sort by couple
    private List<Person> familyList; // sort by family
    private List<Proximity> proximityList;
    private List<Table> tableList;
    private List<Bribe> bribeList;
    private Guest[] allGuests;
    private TableAlgo[] allTables;
    private int[][] relationshipTable;
    private LinkedList<Integer>[] hates;
    private LinkedList<Integer>[] loves;

    private double overallScore;

    private static final String TAG = "MainAlgorithm";

    public MainAlgorithm(int dinnerId){
        this.dinnerId = dinnerId;
        personList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Collections.sort(personList, new SortbyDislikeS());
        coupleList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Collections.sort(coupleList, new SortbyCouple());
        familyList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Collections.sort(familyList, new SortbyFamily());
        proximityList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().proximityDao().loadAllByDinner(dinnerId);
        tableList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Collections.sort(tableList, new SortbyTableSize());
        bribeList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().bribeDao().loadAllByDinner(dinnerId);
        Collections.sort(bribeList, new SortbyBribeSatis());

        dinnerSize = 0;
        overallScore = 0;
        allTables = new TableAlgo[tableList.size()];
        for(int i = 0; i < tableList.size(); i++){
            allTables[i] = new TableAlgo();
            allTables[i].lock = new Boolean[tableList.get(i).getTableSize()];
            allTables[i].seats = new int[tableList.get(i).getTableSize()];
            allTables[i].id = tableList.get(i).getUid();
            allTables[i].seatLeft = tableList.get(i).getTableSize();
            allTables[i].tableSize = tableList.get(i).getTableSize();
            Arrays.fill(allTables[i].lock, false);
            Arrays.fill(allTables[i].seats, -1);
            dinnerSize += tableList.get(i).getTableSize();
        }

        numberOfGuest = personList.size();
        hates = new LinkedList[numberOfGuest];

        for(int i = 0; i < numberOfGuest; i++)
            hates[i] = new LinkedList<Integer>();

        relationshipTable = new int[numberOfGuest][numberOfGuest];
    }

    public double testAll(int[] chs, int start, int len, double best) {
        if (start == len - 1) {
            for (int i = 0; i < chs.length; ++i)
                allGuests[i].seat = chs[i];
            double score = calculateSingleTableScore(0);
            if(best < score)
                best = score;
            return best;
        }
        for (int i = start; i < len; i++) {
            int temp = chs[start];
            chs[start] = chs[i];
            chs[i] = temp;
            best = testAll(chs, start + 1, len, best);
            temp = chs[start];
            chs[start] = chs[i];
            chs[i] = temp;
        }
        return best;
    }

    public void start(){
        step1_hate();
        step2_randomInsertOther();
        for(int i = 0; i < tableList.size(); i++) {
//            Log.i(TAG, "score1: " + calculateSingleTableScore(0));
//            Log.i(TAG, "score2: " + calculateSingleTableScore(1));
            double currentS = calculateSingleTableScore(i);
            tableList.get(i).setScore(currentS);
            overallScore += currentS;
        }
        step3_evolution();
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "guest" + i + ": " + allGuests[i].table + " name: " + personList.get(i).getName() + " seat: " + allGuests[i].seat);
        }
        updateDatabase();
//        Log.i(TAG, "score1: " + calculateSingleTableScore(0));
//        Log.i(TAG, "score2: " + calculateSingleTableScore(1));
//
//        Log.i(TAG, "-------------------------");
//        for(int i = 0; i<10; i++){
//            Log.i(TAG, "Table1:" + tables[0].get(i) + " Name: " + personList.get(tables[0].get(i)).getName() + " Happiness: " + allGuests[tables[0].get(i)].happiness);
//
//        }
//        for(int i = 0; i<10; i++){
//            Log.i(TAG, "Table2:" + tables[1].get(i) + " Name: " + personList.get(tables[1].get(i)).getName() + " Happiness: " + allGuests[tables[1].get(i)].happiness);
//        }
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

    public void updateDatabase(){
        Person currentPerson;
        for (int i = 0; i < numberOfGuest; i++){
            currentPerson = personList.get(i);
            currentPerson.setTableId(tableList.get(allGuests[i].table).getUid());
            currentPerson.setSeatId(allGuests[i].seat);
            DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().updateUsers(currentPerson);
        }
    }

    public void step1_hate(){
        allGuests = new Guest[numberOfGuest];
        for(int i = 0; i < numberOfGuest; i++){
            allGuests[i] = new Guest();
            Log.i(TAG, "current uid==== " + personList.get(i).getId());
            allGuests[i].id = personList.get(i).getId();
            if(personList.get(i).getTableId() != 4 && personList.get(i).isLock()) {
                int tableIndex = 0;
                int currentTableId = tableList.get(0).getUid();
                while(currentTableId != personList.get(i).getTableId()){
                    tableIndex++;
                    currentTableId = tableList.get(tableIndex).getUid();
                }
                allGuests[i].table = tableIndex;
                allGuests[i].seat = personList.get(i).getSeatId();
                allGuests[i].lock = true;
                for(int j = 0; j < tableList.size(); j++){
                    if(allTables[j].id == currentTableId){
                        allTables[j].seats[personList.get(i).getSeatId()] = i;
                        allTables[j].lock[personList.get(i).getSeatId()] = true;
                        Log.i(TAG, "Lock seat:" + personList.get(i).getSeatId());
                        allTables[j].seatLeft--;
                    }
                }
            }
            else {
                allGuests[i].table = -1;
                allGuests[i].seat = -1;
                allGuests[i].lock = false;
            }
            allGuests[i].happiness = 0;
        }

        Log.i(TAG, "numberOfGuest " + numberOfGuest);

        int current1P = -1, current2P = -1;
        boolean found = false;
        long startT = System.nanoTime();
        for(int i = 0; i < proximityList.size(); i++){
            current1P = -1; current2P = -1;
            for(int j = 0; j < numberOfGuest; j++){
                if(allGuests[j].id == proximityList.get(i).guest1Id){
                    current1P = j;
                    if(found){
                        found = false;
                        break;
                    }
                    else  found = true;
                }
                else if(allGuests[j].id == proximityList.get(i).guest2Id){
                    current2P = j;
                    if(found){
                        found = false;
                        break;
                    }
                    else  found = true;
                }
            }
            if(current1P != -1 && current2P != -1) {
                switch (proximityList.get(i).getProximityType()){
                    case 1:
                        hates[current1P].add(current2P);
                        hates[current2P].add(current1P);
                        relationshipTable[current1P][current2P] = -50;
                        break;
                    case 2:
                        relationshipTable[current1P][current2P] = -20;
                        break;
                    case 4:
                        relationshipTable[current1P][current2P] = 20;
                        break;
                    case 5:
                        hates[current1P].add(current2P);
                        relationshipTable[current1P][current2P] = 50;
                        break;
                }
            }
        } // end for
        long elapsedTime = System.nanoTime() - startT;
        Log.i(TAG, "Time used for inserting proximity (nano second): " + (elapsedTime / 1000000000));

        kColorable tryK = new kColorable(numberOfGuest, allGuests, hates, tableList, allTables);
        if(tryK.tryGraphColoring(0)){
            Log.i(TAG, "==========true");
            allGuests = tryK.getGuest();
            allTables = tryK.getTables();
        }
        else {
            Log.i(TAG, "==========false");
            allGuests = tryK.getGuest();
            allTables = tryK.getTables();
        }
    } // end step1_hate

    private void step2_randomInsertOther(){
        for(int i = 0; i < numberOfGuest; i++){
            if(allGuests[i].table == -1){ // if a guest has not been assign
                Log.i(TAG, "left " + i);
                for(int j = 0; j < tableList.size(); j++){
                    if(allTables[j].seatLeft > 0){
                        allGuests[i].table = j;
                        for(int k = 0; i < tableList.get(j).getTableSize(); k++){
                            if(allTables[j].seats[k] == -1){
                                allGuests[j].seat = k;
                                allTables[j].seats[k] = i;
                                allTables[j].seatLeft--;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    } // step2_randomInsertOther

    private TableAlgo makeDeepCopy(TableAlgo old){
        TableAlgo copy = new TableAlgo();
        copy.lock = old.lock;
        copy.seats = new int[old.tableSize];
        copy.id = old.id;
        copy.seatLeft = old.tableSize;
        copy.tableSize = old.tableSize;
        int j = 0;
        for(Integer i : old.seats){
            copy.seats[j] = Integer.valueOf(i);
            j++;
        }
        return copy;
    }

    private double updatingSwap(int n){
        TableAlgo[][] newTables = new TableAlgo[n][tableList.size()];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < tableList.size(); j++){
                newTables[i][j] = makeDeepCopy(allTables[j]);
            }
        }
        double currentBest = overallScore;
        double currentScore;
        int bestSwapTable1 = -1, bestSwapTable2 = -1;
        int currentBestIndex = -1;
        for(int i = 0; i < n; i++){
            int swapTable1, swapTable2, swapPerson1, swapPerson2, swapPerson1Id, swapPerson2Id, swapTable1Type, swapTable2Type;
            swapTable1 = ThreadLocalRandom.current().nextInt(0, tableList.size());
            swapTable2 = ThreadLocalRandom.current().nextInt(0, tableList.size());
            swapPerson1 = ThreadLocalRandom.current().nextInt(0, tableList.get(swapTable1).getTableSize());
            swapPerson2 = ThreadLocalRandom.current().nextInt(0, tableList.get(swapTable2).getTableSize());
            while (allTables[swapTable1].lock[swapPerson1]){
                swapTable1 = ThreadLocalRandom.current().nextInt(0, tableList.size());
                swapPerson1 = ThreadLocalRandom.current().nextInt(0, tableList.get(swapTable1).getTableSize());
            }
            while (allTables[swapTable2].lock[swapPerson2]){
                swapTable2 = ThreadLocalRandom.current().nextInt(0, tableList.size());
                swapPerson2 = ThreadLocalRandom.current().nextInt(0, tableList.get(swapTable2).getTableSize());
            }
            swapPerson1Id = allTables[swapTable1].seats[swapPerson1];
            swapPerson2Id = allTables[swapTable2].seats[swapPerson2];
            newTables[i][swapTable1].seats[swapPerson1] = swapPerson2Id;
            newTables[i][swapTable2].seats[swapPerson2] = swapPerson1Id;
            swapTable1Type = tableList.get(swapTable1).getTableType();
            swapTable2Type = tableList.get(swapTable2).getTableType();
            if(swapTable1 != swapTable2)
                currentScore = overallScore - tableList.get(swapTable1).getScore() - tableList.get(swapTable2).getScore()
                        + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type) + calculateSingleTableScore(newTables[i][swapTable2], swapTable2Type);
            else
                currentScore = overallScore - tableList.get(swapTable1).getScore() + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type);
            if(currentBest < currentScore){
                currentBest = currentScore;
                currentBestIndex = i;
                bestSwapTable1 = swapTable1;
                bestSwapTable2 = swapTable2;
            }
        }
        if(currentBestIndex >= 0){
            allTables = newTables[currentBestIndex];
            tableList.get(bestSwapTable1).setScore(calculateSingleTableScore(bestSwapTable1));
            tableList.get(bestSwapTable2).setScore(calculateSingleTableScore(bestSwapTable2));
        }
        return currentBest;
    }

    public void step3_evolution(){
        double currentBestScore = overallScore;
        int currentBestIndex = 0;
        int evolutionCount = 0;
        while(evolutionCount < 500000 && evolutionCount - currentBestIndex < 5000){
            double c = updatingSwap(30);
            if(c > overallScore){
                currentBestScore = c;
                Log.i(TAG, "FindBest: " + currentBestScore);
                overallScore = currentBestScore;
                currentBestIndex = evolutionCount;
            }
            evolutionCount++;
        }
        for(int i = 0; i < tableList.size(); i++){
            for(int j = 0; j < allTables[i].tableSize; j++){
                if(allTables[i].seats[j] != -1){
                    allGuests[allTables[i].seats[j]].table = i;
                    allGuests[allTables[i].seats[j]].seat = j;
                }
            }
        }
        Log.i(TAG, "currentBestIndex " + currentBestIndex);
        Log.i(TAG, "currentScore " + overallScore);
    } // step3_evolution

//    private double checkLovers(){ // check if any family or couple has been split
//        Person currentPerson;
//        double currentScore;
//        int currentId;
//        for(int i = 0; i < coupleList.size(); i++){
//            currentPerson = coupleList.get(i);
//
//        }
//    }

    private double calculateSingleTableScore(int tableI){
        int currentID = 0;
        int currentPersonHappiness;
        final int tableSize = tableList.get(tableI).getTableSize();
        int tableType = tableList.get(tableI).getTableType();
        //Log.i(TAG, "table:" + tableI + "type:" + tableType);
        double currentScore = 0;
        switch (tableType) {
            case 0:
                for (int i = 0; i < tableSize; i++) {
                    currentID = allTables[tableI].seats[i];
                    if (currentID == -1)
                        continue;
                    if (i == 0) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1]];
                        if (allTables[tableI].seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

            case 1:
                for (int i = 0; i < tableSize; i++) {
                    currentID = allTables[tableI].seats[i];
                    if (currentID == -1)
                        continue;
                    if (i == 0) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1]];
                        if (allTables[tableI].seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        if(allTables[tableI].seats[tableSize - 1 - i] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1 - i]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

            case 2:
                for (int i = 0; i < tableSize; i++) {
                    currentID = allTables[tableI].seats[i];
                    if (currentID == -1)
                        continue;
                    if (i == 0) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1]];
                        if (allTables[tableI].seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[1]];
                        if(allTables[tableI].seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][allTables[tableI].seats[tableSize - i - 2]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    else if(i == (tableSize - 2) / 2) {
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }else{
                        currentPersonHappiness = 0;
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        if(allTables[tableI].seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][allTables[tableI].seats[tableSize - i - 2]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

        }
        return currentScore;
    }

    private double calculateSingleTableScore(TableAlgo tablegiven, int tableType){
        int currentID = 0;
        int currentPersonHappiness;
        final int tableSize = tablegiven.tableSize;
        double currentScore = 0;
        switch (tableType) {
            case 0:
                for (int i = 0; i < tableSize; i++) {
                    currentID = tablegiven.seats[i];
                    if (currentID == -1)
                        continue;
                    if (i == 0) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[tableSize - 1]];
                        if (tablegiven.seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

            case 1:
                for (int i = 0; i < tableSize; i++) {
                    currentID = tablegiven.seats[i];
                    if (currentID == -1)
                        continue;
                    if (i == 0) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[tableSize - 1]];
                        if (tablegiven.seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        if(tablegiven.seats[tableSize - 1 - i] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][tablegiven.seats[tableSize - 1 - i]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

            case 2:
                for (int i = 0; i < tableSize; i++) {
                    currentID = tablegiven.seats[i];
                    if (currentID == -1)
                        continue;
                    if (i == 0) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[tableSize - 1]];
                        if (tablegiven.seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    else if(i == (tableSize - 2) / 2) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }else{
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        if(tablegiven.seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][tablegiven.seats[tableSize - i - 2]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

        }
        return currentScore;
    }

}
