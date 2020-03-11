package com.example.projecr7.CalculateTableAlgorithm;

public class monteCarloTreeSearch {

    private TableAlgo[] allTables;
    private int[][] relationshipTable;

    public monteCarloTreeSearch(TableAlgo[] allTables, int[][] relationshipTable){
        this.allTables = allTables;
        this.relationshipTable = relationshipTable;
    }

    public double search(){
        return 0;
    }

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
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        currentScore += currentPersonHappiness;
                    } else {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
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
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        currentScore += currentPersonHappiness;
                    } else {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        if(tablegiven.seats[tableSize - 1 - i] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][tablegiven.seats[tableSize - 1 - i]]) / 2.0;
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
                        currentScore += currentPersonHappiness;
                    } else if (i == tableSize - 1) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[0] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[0]];
                        currentScore += currentPersonHappiness;
                    }
                    else if(i == (tableSize - 2) / 2) {
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        currentScore += currentPersonHappiness;
                    }else{
                        currentPersonHappiness = 0;
                        if (tablegiven.seats[i - 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i - 1]];
                        if (tablegiven.seats[i + 1] != -1)
                            currentPersonHappiness += (double) relationshipTable[currentID][tablegiven.seats[i + 1]];
                        if(tablegiven.seats[tableSize - i - 2] != -1) // opposite
                            currentPersonHappiness += ((double) relationshipTable[currentID][tablegiven.seats[tableSize - i - 2]]) / 2.0;
                        currentScore += currentPersonHappiness;
                    }
                }
                break;

        }
        return currentScore;
    }

}
