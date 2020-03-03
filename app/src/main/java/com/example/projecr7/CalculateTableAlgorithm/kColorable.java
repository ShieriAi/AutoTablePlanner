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
    // sorted by number of edges
    private Guest[] guest;

    private TableAlgo[] tables;


    public kColorable(int V, Guest[] guest, LinkedList<Integer> edges[], List<Table> tableList, TableAlgo[] tables){
        this.V = V;
        this.edges = edges;
        this.tableList = tableList;
        this.guest = guest;
        this.C = tableList.size();
        this.color = new int[C];
        this.tables = tables;
        for(int i = 0; i < C; i++){
            color[i] = 0;
        }
    }

    public boolean tryGraphColoring(int v) {

        Log.i(TAG, "currentV: " + v);

        if(v == V)
            return true;

        if(guest[v].table != -1){
            v++;
            return tryGraphColoring(v);
        }

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
        if(tables[i].seatLeft == 0)
            return false;

        Iterator<Integer> it = edges[v].iterator() ;
        while (it.hasNext())
        {
            int j = it.next();
            if(guest[j].table == i)
                return false;
        }

        return true;
    }

    public void insertCurrentGuest(int i, int v){
        for(int j = 0; i < tableList.get(i).getTableSize(); j++){
            if(tables[i].seats[j] == -1){
                guest[v].seat = j;
                tables[i].seats[j] = v;
                break;
            }
        }
        tables[i].seatLeft--;
        Log.i(TAG, "TableLeft: " + tables[i].seatLeft);
        guest[v].table = i;
    }

    public void insertLeftGuest(){
        // TODO
    }

    public Guest[] getGuest(){
        return guest;
    }

    public TableAlgo[] getTables() {
        return tables;
    }
}
