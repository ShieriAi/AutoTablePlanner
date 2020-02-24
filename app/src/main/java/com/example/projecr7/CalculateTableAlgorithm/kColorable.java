package com.example.projecr7.CalculateTableAlgorithm;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Table;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class kColorable {
    private int V, C;
    private int[] color;
    private LinkedList<Integer> edges[];
    // sorted by size, small -> large
    private List<Table> tableList;
    private int[] tableSeatsLeft;
    // sorted by number of edges
    private int[][] guest; // [i][0] ->guestId, [i][1] -> guestType, [i][2]guestNumber, [i][3]guestTableId, [i][4]guestSeatId


    public kColorable(int V, int guest[][], LinkedList<Integer> edges[], List<Table> tableList){
        this.V = V;
        this.edges = edges;
        this.tableList = tableList;
        this.guest = guest;
        this.C = tableList.size();
        this.color = new int[C];
        this.tableSeatsLeft = new int[C];
        for(int i = 0; i < C; i++){
            color[i] = 0;
            tableSeatsLeft[i] = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().tableDao().loadSingleById(tableList.get(i).getUid()).getTableSize();
        }
    }

    public boolean tryGraphColoring(int v) {

        if(v == V)
            return true;

        int[] currentGuest = guest[v];

        for(int i = 0; i < C; i++){
            if(tryTable(i)){
                insertCurrentGuest(i);
                return tryGraphColoring(v++);
            }
        }
        return false;
    }

    public boolean tryTable(int i){
        if(tableSeatsLeft[i] < guest[i][2])
            return false;

        Iterator<Integer> it = edges[i].iterator() ;
        while (it.hasNext())
        {
            int j = it.next();
            if(guest[j][3] == tableList.get(i).getUid())
                return false;
        }

        return true;
    }

    public void insertCurrentGuest(int i){
        //if ()
        tableSeatsLeft[i] -= guest[i][2];
        guest[i][3] = tableList.get(i).getUid();
    }

    public void insertLeftGuest(){
        // TODO
    }

    public void getGuest(){

    }


}
