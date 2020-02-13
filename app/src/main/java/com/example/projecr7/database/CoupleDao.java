package com.example.projecr7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CoupleDao {
    @Query("SELECT * FROM couple")
    List<Couple> getAll();

    @Query("SELECT * FROM couple WHERE couple_uid IN (:userIds)")
    List<Couple> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM couple WHERE dinner_id IS (:dinnerId)")
    List<Couple> loadAllByDinner(int dinnerId);

    @Query("SELECT * FROM couple WHERE couple_uid IS (:coupleId)")
    Couple loadSingleById(int coupleId);

//    @Query("SELECT * FROM couple WHERE Dinner_Name LIKE :first")
//    Couple findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Couple.class)
    void insert(Couple couple);

    @Insert(entity = Couple.class)
    void insertAll(Couple... couples);

    @Update(entity = Couple.class)
    void updateUsers(Couple... couples);

    @Delete(entity = Couple.class)
    void delete(Couple couple);
    
}
