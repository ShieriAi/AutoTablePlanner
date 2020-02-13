package com.example.projecr7.database;

import com.example.projecr7.database.Dinner;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface DinnerDao {
    @Query("SELECT * FROM dinner")
    List<Dinner> getAll();

    @Query("SELECT * FROM dinner WHERE uid IN (:userIds)")
    List<Dinner> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM dinner WHERE uid IS (:dinnerId)")
    Dinner loadSingleById(int dinnerId);

//    @Query("SELECT * FROM dinner WHERE Dinner_Name LIKE :first")
//    Dinner findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Dinner.class)
    void insert(Dinner dinner);

    @Insert(entity = Dinner.class)
    void insertAll(Dinner... dinners);

    @Update(entity = Dinner.class)
    void updateUsers(Dinner... dinners);

    @Delete(entity = Dinner.class)
    void delete(Dinner dinner);
}
