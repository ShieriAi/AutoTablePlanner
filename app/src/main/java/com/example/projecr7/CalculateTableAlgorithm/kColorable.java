package com.example.projecr7.CalculateTableAlgorithm;

import android.util.Log;

import com.example.projecr7.MainActivity;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class kColorable {
    private static final String TAG = "kColorable";
    private int V, C;
    private int[] color;
    private LinkedList<Integer> edges[];
    // sorted by size, small -> large
    private List<Table> tableList;
    private int[] tableSeatsLeft;
    // sorted by number of edges
    private int[][] guest; // [i][0] ->guestId, [i][1]guestTableId, [i][2]guestSeatId

    private ArrayList<Integer>[] tables;


    public kColorable(int V, int guest[][], LinkedList<Integer> edges[], List<Table> tableList, ArrayList<Integer>[] tables){
        this.V = V;
        this.edges = edges;
        this.tableList = tableList;
        this.guest = guest;
        this.C = tableList.size();
        this.color = new int[C];
        this.tableSeatsLeft = new int[C];
        this.tables = tables;
        for(int i = 0; i < C; i++){
            color[i] = 0;
            tableSeatsLeft[i] = DatabaseClient.getInstance(MainActivity.getContext()).getAppDatabase().tableDao().loadSingleById(tableList.get(i).getUid()).getTableSize();
        }
        Log.i(TAG, "======edge: " + edges[4]);
    }

    public boolean tryGraphColoring(int v) {

        Log.i(TAG, "currentV: " + v);

        if(v == V)
            return true;

        for(int i = 0; i < C; i++){
            Log.i(TAG, "currentTable: " + i);
            if(tryTable(i,v)){
                Log.i(TAG, "Table: " + i +" success");
                insertCurrentGuest(i,v);
                v++;
                return tryGraphColoring(v);
            }
        }
        return false;
    }

    public boolean tryTable(int i, int v){
        if(tableSeatsLeft[i] == 0)
            return false;

        Iterator<Integer> it = edges[v].iterator() ;
        while (it.hasNext())
        {
            int j = it.next();
            if(guest[j][1] == i)
                return false;
        }

        return true;
    }

    public void insertCurrentGuest(int i, int v){
        guest[v][2] = tableList.get(i).getTableSize() - tableSeatsLeft[i];
        tables[i].set(tableList.get(i).getTableSize() - tableSeatsLeft[i], v);
        tableSeatsLeft[i]--;
        guest[v][1] = i;
    }

    public void insertLeftGuest(){
        // TODO
    }

    public int[][] getGuest(){
        return guest;
    }

    public ArrayList<Integer>[] getTables() {
        return tables;
    }

    public int[] getTableSeatsLeft() {
        return tableSeatsLeft;
    }
}
