package com.example.projecr7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TableDao {

    @Query("SELECT * FROM `table`")
    List<Table> getAll();

    @Query("SELECT * FROM `table` WHERE table_id IN (:tableIds)")
    List<Table> loadAllByIds(int[] tableIds);

    @Query("SELECT * FROM `table` WHERE table_id IS (:tableId)")
    Table loadSingleById(int tableId);

    @Query("SELECT * FROM `table` WHERE dinner_id IS (:dinnerID)")
    List<Table> loadAllByDinner(int dinnerID);

//    @Query("SELECT * FROM dinner WHERE Dinner_Name LIKE :first")
//    Dinner findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Table.class)
    void insert(Table table);

    @Insert(entity = Table.class)
    void insertAll(Table... tables);

    @Update(entity = Table.class)
    void updateUsers(Table... tables);

    @Delete(entity = Table.class)
    void delete(Table table);
}
