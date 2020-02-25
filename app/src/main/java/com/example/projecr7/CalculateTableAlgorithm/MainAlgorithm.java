package com.example.projecr7.CalculateTableAlgorithm;

import android.util.Log;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.database.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    private int dinnerSize, numberOfGuest;
    private List<Person> personList;
    private List<Proximity> proximityList;
    private List<Table> tableList;
    private List<Bribe> bribeList;
    private int[][] guest;
    private int[][] relationshipTable;
    private ArrayList<Integer>[] tables;
    private int[] tableSeatsLeft;

    private double overallScore;

    private static final String TAG = "MainAlgorithm";

    public MainAlgorithm(int dinnerId){
        this.dinnerId = dinnerId;
        personList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        Collections.sort(personList, new SortbyDislikeS());
        proximityList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().proximityDao().loadAllByDinner(dinnerId);
        tableList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().tableDao().loadAllByDinner(dinnerId);
        Collections.sort(tableList, new SortbyTableSize());
        bribeList = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().bribeDao().loadAllByDinner(dinnerId);
        dinnerSize = 0;
        overallScore = 0;
        tableSeatsLeft = new int[tableList.size()];
        tables = new ArrayList[tableList.size()];
        for(int i = 0; i < tableList.size(); i++){
            tables[i] = new ArrayList();
            for(int j = 0; j < tableList.get(i).getTableSize(); j++)
                tables[i].add(-1);
            dinnerSize += tableList.get(i).getTableSize();
            tableSeatsLeft[i] = tableList.get(i).getTableSize();
        }
        numberOfGuest = personList.size();
        relationshipTable = new int[numberOfGuest][numberOfGuest];
    }

    public void start(){
        step1_hate();
        step2_randomInsertOther();
        for(int i = 0; i < tableList.size(); i++) {
            double currentS = calculateSingleTableScore(i);
            tableList.get(i).setScore(currentS);
            overallScore += currentS;
        }
        step3_evolution();
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "guest" + i + ": " + guest[i][1] + " seat: " + guest[i][2]);
        }
        updateDatabase();
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
            currentPerson.setTableId(tableList.get(guest[i][1]).getUid());
            currentPerson.setSeatId(guest[i][2]);
            DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().updateUsers(currentPerson);
        }
    }

    public void step1_hate(){
        guest = new int[numberOfGuest][3];
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "current uid==== " + personList.get(i).getId());
            guest[i][0] = personList.get(i).getId();
            guest[i][1] = -1;
            guest[i][2] = -1;
        }

        Log.i(TAG, "numberOfGuest " + numberOfGuest);

        LinkedList<Integer>[] edges = new LinkedList[numberOfGuest];
        for (int i=0; i<numberOfGuest; ++i)
            edges[i] = new LinkedList();
        int current1P = -1, current2P = -1;
        boolean found = false;
        long startT = System.nanoTime();
        for(int i = 0; i < proximityList.size(); i++){
            current1P = -1; current2P = -1;
            for(int j = 0; j < numberOfGuest; j++){
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
            if(current1P != -1 && current2P != -1) {
                switch (proximityList.get(i).getProximityType()){
                    case 1:
                        edges[current1P].add(current2P);
                        edges[current2P].add(current1P);
                        relationshipTable[current1P][current2P] = -50;
                        break;
                    case 2:
                        relationshipTable[current1P][current2P] = -20;
                        break;
                    case 4:
                        relationshipTable[current1P][current2P] = 20;
                        break;
                    case 5:
                        relationshipTable[current1P][current2P] = 50;
                        break;
                }
            }
        } // end for
        long elapsedTime = System.nanoTime() - startT;
        Log.i(TAG, "Time used for inserting proximity (nano second): " + elapsedTime);

        kColorable tryK = new kColorable(numberOfGuest, guest, edges, tableList, tables);
        if(tryK.tryGraphColoring(0)){
            Log.i(TAG, "==========true");
            guest = tryK.getGuest();
            tables = tryK.getTables();
            tableSeatsLeft = tryK.getTableSeatsLeft();
        }
        else {
            Log.i(TAG, "==========false");
            tryK.insertLeftGuest();
        }
    } // end step1_hate

    public void step2_randomInsertOther(){
        int smallestAvilible = 0;
        for(int i = 0; i < numberOfGuest; i++){
            if(guest[i][1] == -1){ // if a guest has not been assign
                Log.i(TAG, "left " + i);
                for(int j = smallestAvilible; j < tableList.size(); j++){
                    if(tableSeatsLeft[j] > 0){
                        smallestAvilible = j;
                        guest[i][1] = j;
                        guest[i][2] = tableList.get(j).getTableSize() - tableSeatsLeft[j];
                        tables[j].set(tableList.get(j).getTableSize() - tableSeatsLeft[j], i);
                        tableSeatsLeft[j]--;
                        break;
                    }
                }
            }
        }
    } // step2_randomInsertOther

    private ArrayList<Integer> makeDeepCopy(ArrayList<Integer> old){
        ArrayList<Integer> copy = new ArrayList<Integer>(old.size());
        for(Integer i : old){
            copy.add(Integer.valueOf(i));
        }
        return copy;
    }

    private double updatingSwap(int n){
        ArrayList<Integer>[][] newTables = new ArrayList[n][tableList.size()];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < tableList.size(); j++){
                newTables[i][j] = makeDeepCopy(tables[j]);
            }
        }
        double currentBest = overallScore;
        double currentScore;
        int bestSwapTable1 = -1, bestSwapTable2 = -1;
        int currentBestIndex = -1;
        for(int i = 0; i < n; i++){
            int swapTable1, swapTable2, swapPerson1, swapPerson2, swapPerson1Id, swapPerson2Id;
            swapTable1 = ThreadLocalRandom.current().nextInt(0, tableList.size());
            swapTable2 = ThreadLocalRandom.current().nextInt(0, tableList.size());
            swapPerson1 = ThreadLocalRandom.current().nextInt(0, tableList.get(swapTable1).getTableSize());
            swapPerson2 = ThreadLocalRandom.current().nextInt(0, tableList.get(swapTable2).getTableSize());
            swapPerson1Id = tables[swapTable1].get(swapPerson1);
            swapPerson2Id = tables[swapTable2].get(swapPerson2);
            newTables[i][swapTable1].set(swapPerson1, swapPerson2Id);
            newTables[i][swapTable2].set(swapPerson2, swapPerson1Id);
            currentScore = overallScore - tableList.get(swapTable1).getScore() - tableList.get(swapTable2).getScore()
                    + calculateSingleTableScore(newTables[i][swapTable1]) + calculateSingleTableScore(newTables[i][swapTable2]);
            if(currentBest < currentScore){
                currentBest = currentScore;
                currentBestIndex = i;
                bestSwapTable1 = swapTable1;
                bestSwapTable2 = swapTable2;
            }
        }
        if(currentBestIndex >= 0){
            tables = newTables[currentBestIndex];
            tableList.get(bestSwapTable1).setScore(calculateSingleTableScore(bestSwapTable1));
            tableList.get(bestSwapTable2).setScore(calculateSingleTableScore(bestSwapTable2));
        }
        return currentBest;
    }

    public void step3_evolution(){
        double currentBestScore = overallScore;
        int currentBestIndex = 0;
        int evolutionCount = 0;
        while(evolutionCount < 50000 && evolutionCount - currentBestIndex < 500){
            double c = updatingSwap(10);
            if(c > overallScore){
                currentBestScore = c;
                overallScore = currentBestScore;
                currentBestIndex = evolutionCount;
            }
            evolutionCount++;
        }
        for(int i = 0; i < tableList.size(); i++){
            for(int j = 0; j < tables[i].size(); j++){
                if(tables[i].get(j) != -1){
                    guest[tables[i].get(j)][1] = i;
                    guest[tables[i].get(j)][2] = j;
                }
            }
        }
        Log.i(TAG, "currentBestIndex " + currentBestIndex);
        Log.i(TAG, "currentScore " + overallScore);
    } // step3_evolution

    private double calculateSingleTableScore(int tableI){
        int currentID = 0;
        int currentPersonHappiness;
        final int tableSize = tableList.get(tableI).getTableSize();
        double currentScore = 0;
        for(int i = 0; i < tableSize; i++) {
            currentID = tables[tableI].get(i);
            if(currentID == -1)
                continue;
            if(i == 0) {
                currentPersonHappiness = 0;
                if (tables[tableI].get(tableSize - 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tables[tableI].get(tableSize - 1)];
                if (tables[tableI].get(1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tables[tableI].get(1)];
                currentScore += currentPersonHappiness;
            }
            else if(i == tableSize - 1) {
                currentPersonHappiness = 0;
                if (tables[tableI].get(i - 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tables[tableI].get(i - 1)];
                if (tables[tableI].get(0) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tables[tableI].get(0)];
                currentScore += currentPersonHappiness;
            }

            else {
                currentPersonHappiness = 0;
                if (tables[tableI].get(i - 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tables[tableI].get(i - 1)];
                if (tables[tableI].get(i + 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tables[tableI].get(i + 1)];
                currentScore += currentPersonHappiness;
            }
        }
        return currentScore;
    }

    private double calculateSingleTableScore(ArrayList<Integer> tablegiven){
        int currentID = 0;
        int currentPersonHappiness;
        final int tableSize = tablegiven.size();
        double currentScore = 0;
        for(int i = 0; i < tableSize; i++) {
            currentID = tablegiven.get(i);
            if(currentID == -1)
                continue;
            if(i == 0) {
                currentPersonHappiness = 0;
                if (tablegiven.get(tableSize - 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.get(tableSize - 1)];
                if (tablegiven.get(1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.get(1)];
                currentScore += currentPersonHappiness;
            }
            else if(i == tableSize - 1) {
                currentPersonHappiness = 0;
                if (tablegiven.get(i - 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.get(i - 1)];
                if (tablegiven.get(0) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.get(0)];
                currentScore += currentPersonHappiness;
            }

            else {
                currentPersonHappiness = 0;
                if (tablegiven.get(i - 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.get(i - 1)];
                if (tablegiven.get(i + 1) != -1)
                    currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.get(i + 1)];
                currentScore += currentPersonHappiness;
            }
        }
        return currentScore;
    }

}
