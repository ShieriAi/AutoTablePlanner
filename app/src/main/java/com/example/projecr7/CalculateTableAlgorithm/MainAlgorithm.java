package com.example.projecr7.CalculateTableAlgorithm;

import android.util.Log;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.Bribe;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Person;
import com.example.projecr7.database.Proximity;
import com.example.projecr7.database.Table;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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
        return Long.compare(o2.getCoupleId(), o1.getCoupleId());
    }
}

class SortbyFamily implements Comparator<Person>
{
    @Override
    public int compare(Person o1, Person o2) {
        return Long.compare(o2.getFamilyId(), o1.getFamilyId());
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
    int table, seat;
    double happiness;
    long id;
    Boolean lock;
    LinkedList<Guest> loves;
    LinkedList<Guest> hates;
}

class TableAlgo{
    int id, seatLeft, tableSize;
    int[] seats;
    Boolean[] lock;
}

class Solution{
    TableAlgo[] tables;
    double[] scores;
    double score;
}

public class MainAlgorithm {

    private int dinnerId;
    private int dinnerSize, numberOfGuest;
    private Dinner currentDinner;
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

    private int coupleSCount;

    private double overallScore;

    private static final String TAG = "MainAlgorithm";

    public MainAlgorithm(int dinnerId){
        this.dinnerId = dinnerId;
        currentDinner = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().dinnerDao().loadSingleById(dinnerId);
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
        coupleSCount = 0;

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

    public void start(boolean retry){
        initDataStart();
        step1_hate();
        step2_randomInsertOther();
        overallScore = 0;
        for(int i = 0; i < tableList.size(); i++) {
            double currentS = calculateSingleTableScore(i);
            tableList.get(i).setScore(currentS);
            overallScore += currentS;
        }
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "guest" + i + ": " + allGuests[i].table + " name: " + personList.get(i).getName() + " seat: " + allGuests[i].seat);
        }
        Log.i(TAG, "=======-=-=-=-==");
        Solution solution = new Solution();
        solution.score = Double.NEGATIVE_INFINITY;
        Solution currentSolution;
        for(int i = 0; i < 10; i++) {
            currentSolution = step3_evolution();
            if(currentSolution.score > solution.score)
                solution = currentSolution;
        }
        allTables = solution.tables;
        overallScore = solution.score;
        if(!retry) {
            for (int i = 0; i < tableList.size(); i++) {
                calculateSingleTableScore(i);
                for (int j = 0; j < allTables[i].tableSize; j++) {
                    if (allTables[i].seats[j] != -1) {
                        allGuests[allTables[i].seats[j]].table = i;
                        allGuests[allTables[i].seats[j]].seat = j;
                    }
                }
            }
            updateDatabase();
        }
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "guest" + i + ": " + allGuests[i].table + " name: " + personList.get(i).getName() + " seat: " + allGuests[i].seat);
        }

        Log.i(TAG, "couple count: " + coupleSCount);
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

    public void retry(){
        double preScore = currentDinner.getScore();
        initDataStart();
        step1_hate();
        step2_randomInsertOther();
        overallScore = 0;
        for(int i = 0; i < tableList.size(); i++) {
            double currentS = calculateSingleTableScore(i);
            tableList.get(i).setScore(currentS);
            overallScore += currentS;
        }
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "guest" + i + ": " + allGuests[i].table + " name: " + personList.get(i).getName() + " seat: " + allGuests[i].seat);
        }
        Log.i(TAG, "=======-=-=-=-==");
        Solution solution = new Solution();
        solution.score = Double.NEGATIVE_INFINITY;
        Solution currentSolution;
        for(int i = 0; i < 10; i++) {
            currentSolution = step3_evolution();
            if(currentSolution.score > solution.score)
                solution = currentSolution;
        }
        allTables = solution.tables;
        overallScore = solution.score;
        if(overallScore > preScore) {
            for (int i = 0; i < tableList.size(); i++) {
                calculateSingleTableScore(i);
                for (int j = 0; j < allTables[i].tableSize; j++) {
                    if (allTables[i].seats[j] != -1) {
                        allGuests[allTables[i].seats[j]].table = i;
                        allGuests[allTables[i].seats[j]].seat = j;
                    }
                }
            }
            updateDatabase();
        }
        for(int i = 0; i < numberOfGuest; i++){
            Log.i(TAG, "guest" + i + ": " + allGuests[i].table + " name: " + personList.get(i).getName() + " seat: " + allGuests[i].seat);
        }

        Log.i(TAG, "couple count: " + coupleSCount);
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

//    public void retry(){
//        start(true);
//        Solution solution = new Solution();
//        solution.score = overallScore;
//        solution.tables = new TableAlgo[tableList.size()];
//        for(int i = 0; i < tableList.size(); i++)
//            solution.tables[i] = makeDeepCopy(allTables[i]);
//        Log.i(TAG, "INIT DATA===");
//        allTables = new TableAlgo[tableList.size()];
//        overallScore = 0;
//        for(int i = 0; i < tableList.size(); i++){
//            allTables[i] = new TableAlgo();
//            allTables[i].lock = new Boolean[tableList.get(i).getTableSize()];
//            allTables[i].seats = new int[tableList.get(i).getTableSize()];
//
//            allTables[i].id = tableList.get(i).getUid();
//            allTables[i].seatLeft = tableList.get(i).getTableSize();
//            allTables[i].tableSize = tableList.get(i).getTableSize();
//            Arrays.fill(allTables[i].lock, false);
//            Arrays.fill(allTables[i].seats, -1);
//        }
//        initDataRetry();
//        Log.i(TAG, "EVOLUTION===");
//        Solution oldSolutionUpdate = step3_evolution();
//        if(oldSolutionUpdate.score > solution.score) {
//            Log.i(TAG, "new Solution:" + oldSolutionUpdate.score);
//            allTables = oldSolutionUpdate.tables;
//            overallScore = oldSolutionUpdate.score;
//        }
//        else {
//            allTables = solution.tables;
//            overallScore = solution.score;
//        }
//        for(int i = 0; i < tableList.size(); i++){
//            calculateSingleTableScore(i);
//            for(int j = 0; j < allTables[i].tableSize; j++){
//                if(allTables[i].seats[j] != -1){
//                    allGuests[allTables[i].seats[j]].table = i;
//                    allGuests[allTables[i].seats[j]].seat = j;
//                }
//            }
//        }
//
//        Log.i(TAG, "UPDATING===");
//        updateDatabase();
//    }

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
            currentPerson.setHappiness(allGuests[i].happiness);
            DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().personDao().updateUsers(currentPerson);
        }
        currentDinner.setScore(overallScore);
        DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().dinnerDao().updateUsers(currentDinner);
    }

    public void initDataRetry(){
        Log.i(TAG, "retrying");
        overallScore = currentDinner.getScore();
        allGuests = new Guest[numberOfGuest];
        for(int i = 0; i < numberOfGuest; i++){
            allGuests[i] = new Guest();
            Log.i(TAG, "current uid==== " + personList.get(i).getId());
            allGuests[i].id = personList.get(i).getId();
            int tableIndex = 0;
            int currentTableId = tableList.get(0).getUid();
            while(currentTableId != personList.get(i).getTableId()){
                tableIndex++;
                currentTableId = tableList.get(tableIndex).getUid();
            }
            allGuests[i].table = tableIndex;
            allGuests[i].seat = personList.get(i).getSeatId();
            allGuests[i].lock = personList.get(i).isLock();
            for(int j = 0; j < tableList.size(); j++){
                if(allTables[j].id == currentTableId){
                    allTables[j].seats[personList.get(i).getSeatId()] = i;
                    allTables[j].lock[personList.get(i).getSeatId()] = allGuests[i].lock;
                    Log.i(TAG, "Lock seat:" + personList.get(i).getSeatId());
                    allTables[j].seatLeft--;
                }
            }
            allGuests[i].happiness = 0;
        }

        Log.i(TAG, "numberOfGuest " + numberOfGuest);
    }

    public void initDataStart(){
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
    }

    public void step1_hate(){
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
//                Log.i(TAG, "left " + i);
//                Log.i(TAG, "left1: " + allTables[0].seatLeft +", "+ allTables[0].seats[9]);
                for(int j = 0; j < tableList.size(); j++){
                    if(allTables[j].seatLeft > 0){
                        allGuests[i].table = j;
                        for(int k = 0; k < tableList.get(j).getTableSize(); k++){
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

    private int findLeft(int tableI, int personI){
        if(personI > 0)
            return personI - 1;
        else
            return tableList.get(tableI).getTableSize() - 1;
    }

    private int findRight(int tableI, int personI){
        if(personI < tableList.get(tableI).getTableSize() - 1)
            return personI + 1;
        else
            return 0;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private Solution updatingSwapCouple(int n, Solution solution){
//        Log.i(TAG, "updating=================================================");
        TableAlgo[][] newTables = new TableAlgo[n][tableList.size()];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < tableList.size(); j++){
                newTables[i][j] = makeDeepCopy(solution.tables[j]);
            }
        }
        double currentBest = solution.score;
        double currentScore;
        int bestSwapTable1 = -1, bestSwapTable2 = -1;
        int currentBestIndex = -1;
        int isCouple1 = 0; // 0 is not, 1 is left, 2 is right
        int isCouple2 = 0; // 0 is not, 1 is left, 2 is right
        int swapTable1 = -1, swapTable2 = -1, swapPerson1 = -1, swapPerson2 = -1, swapPerson1Id = -1, swapPerson2Id = -1, swapTable1Type = -1, swapTable2Type = -1;
        for(int i = 0; i < n; i++){
            isCouple1 = 0; isCouple2 = 0; currentScore = 0;
            swapTable1 = -1; swapTable2 = -1; swapPerson1 = -1; swapPerson2 = -1; swapPerson1Id = -1; swapPerson2Id = -1; swapTable1Type = -1; swapTable2Type = -1;
            swapTable1 = getRandomNumberInRange(0, tableList.size() - 1);
            swapTable2 = getRandomNumberInRange(0, tableList.size() - 1);
            swapPerson1 = getRandomNumberInRange(0, tableList.get(swapTable1).getTableSize() - 1);
            swapPerson2 = getRandomNumberInRange(0, tableList.get(swapTable2).getTableSize() - 1);
            int lockCount = 0;
            while (solution.tables[swapTable1].lock[swapPerson1] && lockCount < 500){
//                Log.i(TAG, "lock1=================================================" + lockCount);
                swapTable1 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson1 = getRandomNumberInRange(0, tableList.get(swapTable1).getTableSize() - 1);
                lockCount++;
            }
            lockCount = 0;
            while (solution.tables[swapTable2].lock[swapPerson2] && lockCount < 500){
//                Log.i(TAG, "lock2=================================================" + lockCount);
                swapTable2 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson2 = getRandomNumberInRange(0, tableList.get(swapTable2).getTableSize() - 1);
                lockCount++;
            }
            swapPerson1Id = solution.tables[swapTable1].seats[swapPerson1];
            swapPerson2Id = solution.tables[swapTable2].seats[swapPerson2];
            int swapPerson1LeftId , swapPerson1RightId , swapPerson2LeftId , swapPerson2RightId;
            swapPerson1LeftId =  solution.tables[swapTable1].seats[findLeft(swapTable1, swapPerson1)]; swapPerson1RightId =  solution.tables[swapTable1].seats[findRight(swapTable1, swapPerson1)];
            swapPerson2LeftId =  solution.tables[swapTable2].seats[findLeft(swapTable2, swapPerson2)]; swapPerson2RightId =  solution.tables[swapTable2].seats[findRight(swapTable2, swapPerson2)];
            if(swapPerson1Id == -1)
                isCouple1 = 0;
            else if(personList.get(swapPerson1Id).getCoupleId() != 4){
                if(swapPerson1LeftId == -1) ;
                else if(personList.get(swapPerson1LeftId).getCoupleId() == personList.get(swapPerson1Id).getCoupleId())
                    isCouple1 = 1;
                else if(swapPerson1RightId == -1) ;
                else if(personList.get(swapPerson1RightId).getCoupleId() == personList.get(swapPerson1Id).getCoupleId())
                    isCouple1 = 2;
            }
            if(swapPerson2Id == -1)
                isCouple2 = 0;
            else if(personList.get(swapPerson2Id).getCoupleId() != 4){
                if(swapPerson2LeftId == -1) ;
                else if(personList.get(swapPerson2LeftId).getCoupleId() == personList.get(swapPerson2Id).getCoupleId())
                    isCouple2 = 1;
                else if(swapPerson2RightId == -1) ;
                else if(personList.get(swapPerson2RightId).getCoupleId() == personList.get(swapPerson2Id).getCoupleId())
                    isCouple2 = 2;
            }
            if(swapTable1 == swapTable2 && (findLeft(swapTable1,swapPerson1) == findRight(swapTable2,swapPerson2) || findRight(swapTable1,swapPerson1) == findLeft(swapTable2,swapPerson2))){
                isCouple1 = 0; isCouple2 = 0;
            }
            coupleSCount++;
           // Log.i(TAG, "i=" + i + " swaping coupele, " + isCouple1 + "&" + isCouple2);
            if(isCouple1 != 0 && isCouple2 != 0){
                //Log.i(TAG, "i=" + i + " swaping coupele, " + isCouple1 + "&" + isCouple2);
                newTables[i][swapTable1].seats[swapPerson1] = swapPerson2Id;
                newTables[i][swapTable2].seats[swapPerson2] = swapPerson1Id;
                if(isCouple1 == 1 && isCouple2 == 1) {
                    newTables[i][swapTable1].seats[findLeft(swapTable1, swapPerson1)] = swapPerson2LeftId;
                    newTables[i][swapTable2].seats[findLeft(swapTable2, swapPerson2)] = swapPerson1LeftId;
                }
                else if(isCouple1 == 2 && isCouple2 == 1) {
                    newTables[i][swapTable1].seats[findRight(swapTable1, swapPerson1)] = swapPerson2LeftId;
                    newTables[i][swapTable2].seats[findLeft(swapTable2, swapPerson2)] = swapPerson1RightId;
                }
                else if(isCouple1 == 1 && isCouple2 == 2) {
                    newTables[i][swapTable1].seats[findLeft(swapTable1, swapPerson1)] = swapPerson2RightId;
                    newTables[i][swapTable2].seats[findRight(swapTable2, swapPerson2)] = swapPerson1LeftId;
                }
                else if(isCouple1 == 2 && isCouple2 == 2) {
                    newTables[i][swapTable1].seats[findRight(swapTable1, swapPerson1)] = swapPerson2RightId;
                    newTables[i][swapTable2].seats[findRight(swapTable2, swapPerson2)] = swapPerson1RightId;
                }
            }
            else if(isCouple1 == 0 && isCouple2 != 0){
                newTables[i][swapTable1].seats[swapPerson1] = swapPerson2Id;
                newTables[i][swapTable2].seats[swapPerson2] = swapPerson1Id;
                if(isCouple2 == 1) {
                    newTables[i][swapTable1].seats[findRight(swapTable1, swapPerson1)] = swapPerson2LeftId;
                    newTables[i][swapTable2].seats[findLeft(swapTable2, swapPerson2)] = swapPerson1RightId;
                }
                else if(isCouple2 == 2) {
                    newTables[i][swapTable1].seats[findLeft(swapTable1, swapPerson1)] = swapPerson2RightId;
                    newTables[i][swapTable2].seats[findRight(swapTable2, swapPerson2)] = swapPerson1LeftId;
                }
            }
            else if(isCouple1 != 0 && isCouple2 == 0){
                newTables[i][swapTable1].seats[swapPerson1] = swapPerson2Id;
                newTables[i][swapTable2].seats[swapPerson2] = swapPerson1Id;
                if(isCouple1 == 1) {
                    newTables[i][swapTable1].seats[findLeft(swapTable1, swapPerson1)] = swapPerson2RightId;
                    newTables[i][swapTable2].seats[findRight(swapTable2, swapPerson2)] = swapPerson1LeftId;
                }
                else if(isCouple1 == 2) {
                    newTables[i][swapTable1].seats[findRight(swapTable1, swapPerson1)] = swapPerson2LeftId;
                    newTables[i][swapTable2].seats[findLeft(swapTable2, swapPerson2)] = swapPerson1RightId;
                }
            }
            else {
                coupleSCount--;
                newTables[i][swapTable1].seats[swapPerson1] = swapPerson2Id;
                newTables[i][swapTable2].seats[swapPerson2] = swapPerson1Id;
            }
            swapTable1Type = tableList.get(swapTable1).getTableType();
            swapTable2Type = tableList.get(swapTable2).getTableType();
            if (swapTable1 != swapTable2)
                currentScore = solution.score - solution.scores[swapTable1] - solution.scores[swapTable2]
                        + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type) + calculateSingleTableScore(newTables[i][swapTable2], swapTable2Type);
            else
                currentScore = overallScore - solution.scores[swapTable1]  + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type);

            if(currentBest < currentScore){
                currentBest = currentScore;
                currentBestIndex = i;
                bestSwapTable1 = swapTable1;
                bestSwapTable2 = swapTable2;
            }
        }
        if(currentBestIndex >= 0){
//            Log.i(TAG, "=-=-=-=-=-=-=-=-=-=-=-");
//            Log.i(TAG, "iscouple1 = " + isCouple1 + " iscouple2 = " + isCouple2);
//            Log.i(TAG, "swaptable1 = " + swapTable1 + " swaptable2 = " + swapTable2);
//            Log.i(TAG, "swapPerson1 = " + swapPerson1 + " swapPerson2 = " + swapPerson2);
//            for(int i = 0; i < solution.tables.length; i++){
//                for(int j = 0; j < solution.tables[i].tableSize; j++)
//                    Log.i(TAG, "table" + i  + " seat " + j + ": " + solution.tables[i].seats[j] + " name: " + personList.get(solution.tables[i].seats[j]).getName() + " coupleId: " + personList.get(solution.tables[i].seats[j]).getCoupleId() );
//            }
            solution.tables = newTables[currentBestIndex];
            solution.scores[bestSwapTable1] = calculateSingleTableScore(newTables[currentBestIndex][bestSwapTable1], tableList.get(bestSwapTable1).getTableType());
            solution.scores[bestSwapTable2] = calculateSingleTableScore(newTables[currentBestIndex][bestSwapTable2], tableList.get(bestSwapTable2).getTableType());
            solution.score = currentBest;
            Log.i(TAG, "currentBest:" + currentBest);
        }
//        Log.i(TAG, "updating end---------------------------------------");
        return solution;
    }

    private Solution updatingSwap3(int n, Solution solution){
//        Log.i(TAG, "updating=================================================");
        TableAlgo[][] newTables = new TableAlgo[n][tableList.size()];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < tableList.size(); j++){
                newTables[i][j] = makeDeepCopy(solution.tables[j]);
            }
        }
        double currentBest = solution.score;
        double currentScore;
        int bestSwapTable1 = -1, bestSwapTable2 = -1, bestSwapTable3 = -1;
        int currentBestIndex = -1;
        int swapTable1 = -1, swapTable2 = -1, swapTable3 = -1, swapPerson1 = -1, swapPerson2 = -1, swapPerson3 = -1, swapPerson1Id = -1, swapPerson2Id = -1, swapPerson3Id = -1;
        for(int i = 0; i < n; i++){
            currentScore = 0;
            swapTable1 = -1; swapTable2 = -1; swapTable3 = -1; swapPerson1 = -1; swapPerson2 = -1; swapPerson3 = -1; swapPerson1Id = -1; swapPerson2Id = -1; swapPerson3Id = -1;
            swapTable1 = getRandomNumberInRange(0, tableList.size() - 1);
            swapTable2 = getRandomNumberInRange(0, tableList.size() - 1);
            swapTable3 = getRandomNumberInRange(0, tableList.size() - 1);
            swapPerson1 = getRandomNumberInRange(0, tableList.get(swapTable1).getTableSize() - 1);
            swapPerson2 = getRandomNumberInRange(0, tableList.get(swapTable2).getTableSize() - 1);
            swapPerson3 = getRandomNumberInRange(0, tableList.get(swapTable3).getTableSize() - 1);
            if(swapTable1 == swapTable2 && swapPerson1 == swapPerson2)
                continue;
            if(swapTable1 == swapTable3 && swapPerson1 == swapPerson3)
                continue;
            if(swapTable2 == swapTable3 && swapPerson2 == swapPerson3)
                continue;
            int lockCount = 0;
            while (solution.tables[swapTable1].lock[swapPerson1] && lockCount < 500){
                swapTable1 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson1 = getRandomNumberInRange(0, tableList.get(swapTable1).getTableSize() - 1);
                lockCount++;
            }
            while (solution.tables[swapTable2].lock[swapPerson2] && lockCount < 500){
                swapTable2 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson2 = getRandomNumberInRange(0, tableList.get(swapTable2).getTableSize() - 1);
                lockCount++;
            }
            while (solution.tables[swapTable3].lock[swapPerson3] && lockCount < 500){
                swapTable3 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson3 = getRandomNumberInRange(0, tableList.get(swapTable3).getTableSize() - 1);
                lockCount++;
            }
            swapPerson1Id = solution.tables[swapTable1].seats[swapPerson1];
            swapPerson2Id = solution.tables[swapTable2].seats[swapPerson2];
            swapPerson3Id = solution.tables[swapTable3].seats[swapPerson3];

            newTables[i][swapTable1].seats[swapPerson1] = swapPerson3Id;
            newTables[i][swapTable2].seats[swapPerson2] = swapPerson1Id;
            newTables[i][swapTable3].seats[swapPerson3] = swapPerson2Id;

            int swapTable1Type, swapTable2Type, swapTable3Type;
            swapTable1Type = tableList.get(swapTable1).getTableType();
            swapTable2Type = tableList.get(swapTable2).getTableType();
            swapTable3Type = tableList.get(swapTable3).getTableType();

            if(swapTable1 == swapTable2 && swapTable1 == swapTable3)
                currentScore = solution.score - solution.scores[swapTable1] + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type);
            else if(swapTable1 == swapTable2)
                currentScore = solution.score - solution.scores[swapTable1] - solution.scores[swapTable3]
                        + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type) + calculateSingleTableScore(newTables[i][swapTable3], swapTable3Type);
            else if(swapTable1 == swapTable3)
                currentScore = solution.score - solution.scores[swapTable2] - solution.scores[swapTable3]
                        + calculateSingleTableScore(newTables[i][swapTable2], swapTable2Type) + calculateSingleTableScore(newTables[i][swapTable3], swapTable3Type);
            else if(swapTable2 == swapTable3)
                currentScore = solution.score - solution.scores[swapTable2] - solution.scores[swapTable1]
                        + calculateSingleTableScore(newTables[i][swapTable2], swapTable2Type) + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type);
            else
                currentScore = solution.score - solution.scores[swapTable1] - solution.scores[swapTable2] - solution.scores[swapTable3]
                    + calculateSingleTableScore(newTables[i][swapTable1], swapTable1Type) + calculateSingleTableScore(newTables[i][swapTable2], swapTable2Type) + calculateSingleTableScore(newTables[i][swapTable3], swapTable3Type);

            if(currentBest < currentScore){
                currentBest = currentScore;
                currentBestIndex = i;
                bestSwapTable1 = swapTable1;
                bestSwapTable2 = swapTable2;
                bestSwapTable3 = swapTable3;
            }
        }
        if(currentBestIndex >= 0){
//            Log.i(TAG, "=-=-=-=-=-=-=-=-=-=-=-");
//            Log.i(TAG, "iscouple1 = " + isCouple1 + " iscouple2 = " + isCouple2);
//            Log.i(TAG, "swaptable1 = " + swapTable1 + " swaptable2 = " + swapTable2);
//            Log.i(TAG, "swapPerson1 = " + swapPerson1 + " swapPerson2 = " + swapPerson2);
//            for(int i = 0; i < solution.tables.length; i++){
//                for(int j = 0; j < solution.tables[i].tableSize; j++)
//                    Log.i(TAG, "table" + i  + " seat " + j + ": " + solution.tables[i].seats[j] + " name: " + personList.get(solution.tables[i].seats[j]).getName() + " coupleId: " + personList.get(solution.tables[i].seats[j]).getCoupleId() );
//            }
            solution.tables = newTables[currentBestIndex];
//            tableList.get(bestSwapTable1).setScore(calculateSingleTableScore(bestSwapTable1));
//            tableList.get(bestSwapTable2).setScore(calculateSingleTableScore(bestSwapTable2));
            solution.scores[bestSwapTable1] = calculateSingleTableScore(newTables[currentBestIndex][bestSwapTable1], tableList.get(bestSwapTable1).getTableType());
            solution.scores[bestSwapTable2] = calculateSingleTableScore(newTables[currentBestIndex][bestSwapTable2], tableList.get(bestSwapTable2).getTableType());
            solution.scores[bestSwapTable3] = calculateSingleTableScore(newTables[currentBestIndex][bestSwapTable3], tableList.get(bestSwapTable3).getTableType());
            solution.score = currentBest;
            //Log.i(TAG, "currentBest:" + currentBest + ", best:" + bestSwapTable1 + "," +bestSwapTable2 + ","+bestSwapTable3);
        }
//        Log.i(TAG, "updating end---------------------------------------");
        return solution;
    }

    private double updatingSwap(int n){
//        Log.i(TAG, "updating=================================================");
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
            swapTable1 = getRandomNumberInRange(0, tableList.size() - 1);
            swapTable2 = getRandomNumberInRange(0, tableList.size() - 1);
            swapPerson1 = getRandomNumberInRange(0, tableList.get(swapTable1).getTableSize() - 1);
            swapPerson2 = getRandomNumberInRange(0, tableList.get(swapTable2).getTableSize() - 1);
            //Log.i(TAG, "swapTable1:" + swapTable1 + ", " + swapPerson1 + "swapTable2:" + swapTable2 + ", " + swapPerson2);
            while (allTables[swapTable1].lock[swapPerson1]){
                swapTable1 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson1 = getRandomNumberInRange(0, tableList.get(swapTable1).getTableSize() - 1);
            }
            while (allTables[swapTable2].lock[swapPerson2]){
                swapTable2 = getRandomNumberInRange(0, tableList.size() - 1);
                swapPerson2 = getRandomNumberInRange(0, tableList.get(swapTable2).getTableSize() - 1);
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
            overallScore = currentBest;
        }
//        Log.i(TAG, "updating end---------------------------------------");
        return currentBest;
    }

    public Solution step3_evolution(){
        Solution solution = new Solution();
        solution.score = overallScore;
        solution.tables = new TableAlgo[tableList.size()];
        solution.scores = new double[tableList.size()];
        for(int i = 0; i < tableList.size(); i++){
            solution.tables[i] = makeDeepCopy(allTables[i]);
            solution.scores[i] = calculateSingleTableScore(i);
        }
        double currentBestScore = overallScore;
        int currentBestIndex = 0;
        int evolutionCount = 1;
        while(evolutionCount < 50000 && evolutionCount - currentBestIndex < 200){
            //Log.i(TAG, "count"+ evolutionCount);
            Solution c = updatingSwapCouple(50, solution);
            Solution c2 = updatingSwap3(50, solution);
            if(c.score > currentBestScore){
                currentBestScore = c.score;
                solution = c;
                Log.i(TAG, "FindBest: " + currentBestIndex);
                currentBestIndex = evolutionCount;
            }
            if(c2.score > currentBestScore){
                currentBestScore = c2.score;
                solution = c2;
                Log.i(TAG, "FindBest2222222222222: " + currentBestIndex);
                currentBestIndex = evolutionCount;
            }
            evolutionCount++;
        }
//        evolutionCount = 1; currentBestIndex = 0;
//        while(evolutionCount < 50000 && evolutionCount - currentBestIndex < 200){
//            //Log.i(TAG, "count"+ evolutionCount);
//            Solution c = updatingSwap3(50, solution);
//            if(c.score > currentBestScore){
//                currentBestScore = c.score;
//                solution = c;
//                Log.i(TAG, "FindBest222: " + currentBestIndex);
//                currentBestIndex = evolutionCount;
//            }
//            evolutionCount++;
//        }
        Log.i(TAG, "======final score:" + currentBestScore);
        //overallScore = currentBestScore;

//        for(int i = 0; i < tableList.size(); i++){
//            for(int j = 0; j < allTables[i].tableSize; j++){
//                if(allTables[i].seats[j] != -1){
//                    allGuests[allTables[i].seats[j]].table = i;
//                    allGuests[allTables[i].seats[j]].seat = j;
//                }
//            }
//        }
        Log.i(TAG, "currentBestIndex " + currentBestIndex + ", count: " + evolutionCount);
        Log.i(TAG, "currentScore " + overallScore);
        return solution;
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
        int isHate[] = new int[personList.size()];
        Arrays.fill(isHate, 0);
        for (int i = 0; i < tableSize; i++){
            currentID = allTables[tableI].seats[i];
            if(currentID == -1)
                continue;
            Iterator<Integer> it = hates[currentID].iterator();
            while (it.hasNext()) {
                int j = it.next();
                isHate[j]++;
            }
        }
        for (int i = 0; i < tableSize; i++) {
            currentID = allTables[tableI].seats[i];
            currentPersonHappiness = 0;
            if (currentID == -1)
                continue;

            currentPersonHappiness -= isHate[currentID] * 50;

            switch (tableType) {
                case 0:
                    if (i == 0) {
                        if (allTables[tableI].seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1]];
                        if (allTables[tableI].seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    break;

                case 1:
                    if (i == 0) {
                        if (allTables[tableI].seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1]];
                        if (allTables[tableI].seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        if (allTables[tableI].seats[tableSize - 1 - i] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1 - i]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    break;

                case 2:
                    if (i == 0) {
                        if (allTables[tableI].seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[tableSize - 1]];
                        if (allTables[tableI].seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[1]];
                        if (allTables[tableI].seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][allTables[tableI].seats[tableSize - i - 2]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == (tableSize - 2) / 2) {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        if (allTables[tableI].seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i - 1]];
                        if (allTables[tableI].seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][allTables[tableI].seats[i + 1]];
                        if (allTables[tableI].seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][allTables[tableI].seats[tableSize - i - 2]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    break;

            }
        }
        return currentScore;
    }

    private double calculateSingleTableScore(TableAlgo tablegiven, int tableType){
        int currentID = 0;
        int currentPersonHappiness;
        final int tableSize = tablegiven.tableSize;
        double currentScore = 0;
        int isHate[] = new int[personList.size()];
        Arrays.fill(isHate, 0);
        for (int i = 0; i < tableSize; i++){
            currentID = tablegiven.seats[i];
            if(currentID == -1)
                continue;
            Iterator<Integer> it = hates[currentID].iterator();
            while (it.hasNext()) {
                int j = it.next();
                isHate[j]++;
            }
        }
        for (int i = 0; i < tableSize; i++) {
            currentID = tablegiven.seats[i];
            currentPersonHappiness = 0;
            if (currentID == -1)
                continue;
            currentPersonHappiness -= isHate[currentID] * 50;
            switch (tableType) {
                case 0:
                    if (i == 0) {
                        if (tablegiven.seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[tableSize - 1]];
                        if (tablegiven.seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    break;

                case 1:
                    if (i == 0) {
                        if (tablegiven.seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[tableSize - 1]];
                        if (tablegiven.seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        if (tablegiven.seats[tableSize - 1 - i] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][tablegiven.seats[tableSize - 1 - i]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    break;

                case 2:
                    if (i == 0) {
                        if (tablegiven.seats[tableSize - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[tableSize - 1]];
                        if (tablegiven.seats[1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else if (i == (tableSize - 2) / 2) {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    } else {
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        if (tablegiven.seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][tablegiven.seats[tableSize - i - 2]]) / 2.0;
                        allGuests[currentID].happiness = currentPersonHappiness;
                        currentScore += currentPersonHappiness;
                    }
                    break;

            }
        }
        return currentScore;
    }

}
