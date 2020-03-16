package com.example.projecr7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProximityDao {

    @Query("SELECT * FROM `proximity`")
    List<Proximity> getAll();

    @Query("SELECT * FROM `proximity` WHERE proximity_uid IN (:proximityIds)")
    List<Proximity> loadAllByIds(int[] proximityIds);

    @Query("SELECT * FROM `proximity` WHERE proximity_uid IS (:proximityId)")
    Proximity loadSingleById(int proximityId);

    @Query("SELECT * FROM `proximity` WHERE dinner_id IS (:dinnerID) ORDER BY type_1 DESC")
    List<Proximity> loadAllByDinner(int dinnerID);

    @Query("SELECT * FROM `proximity` WHERE guest1_Id IS (:guestId) ORDER BY proximity_type ASC")
    List<Proximity> loadAllByGuest1(Long guestId);

//    @Query("SELECT * FROM dinner WHERE Dinner_Name LIKE :first")
//    Dinner findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Proximity.class)
    void insert(Proximity proximity);

    @Insert(entity = Proximity.class)
    void insertAll(Proximity... proximitys);

    @Update(entity = Proximity.class)
    void updateUsers(Proximity... proximitys);

    @Delete(entity = Proximity.class)
    void delete(Proximity proximity);
}
