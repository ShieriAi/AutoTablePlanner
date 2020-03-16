package com.example.projecr7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FamilyDao {
    @Query("SELECT * FROM family")
    List<Family> getAll();

    @Query("SELECT * FROM family WHERE family_uid IN (:userIds)")
    List<Family> loadAllByIds(long[] userIds);

    @Query("SELECT * FROM family WHERE dinner_id IS (:dinnerId) ORDER BY family_size DESC")
    List<Family> loadAllByDinner(int dinnerId);

    @Query("SELECT * FROM family WHERE family_uid IS (:familyId)")
    Family loadSingleById(long familyId);

//    @Query("SELECT * FROM family WHERE Dinner_Name LIKE :first")
//    Family findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Family.class)
    void insert(Family family);

    @Insert(entity = Family.class)
    void insertAll(Family... familys);

    @Update(entity = Family.class)
    void updateUsers(Family... familys);

    @Delete(entity = Family.class)
    void delete(Family family);

}
