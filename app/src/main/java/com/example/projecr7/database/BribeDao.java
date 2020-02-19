package com.example.projecr7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BribeDao {
    @Query("SELECT * FROM bribe")
    List<Bribe> getAll();

    @Query("SELECT * FROM bribe WHERE bribe_uid IN (:userIds)")
    List<Bribe> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM bribe WHERE dinner_id IS (:dinnerId)")
    List<Bribe> loadAllByDinner(int dinnerId);

    @Query("SELECT * FROM bribe WHERE guest_Id IS (:guestId)")
    List<Bribe> loadAllByGuest(int guestId);

    @Query("SELECT * FROM bribe WHERE bribe_uid IS (:bribeId)")
    Bribe loadSingleById(int bribeId);

//    @Query("SELECT * FROM bribe WHERE Dinner_Name LIKE :first")
//    Bribe findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Bribe.class)
    void insert(Bribe bribe);

    @Insert(entity = Bribe.class)
    void insertAll(Bribe... bribes);

    @Update(entity = Bribe.class)
    void updateUsers(Bribe... bribes);

    @Delete(entity = Bribe.class)
    void delete(Bribe bribe);

}
