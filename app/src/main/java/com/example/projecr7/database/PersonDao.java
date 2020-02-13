package com.example.projecr7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PersonDao {
    @Query("SELECT * FROM person")
    List<Person> getAll();

    @Query("SELECT * FROM person WHERE person_uid IN (:peopleIds)")
    List<Person> loadAllByIds(int[] peopleIds);

    @Query("SELECT * FROM person WHERE person_uid IS (:personId)")
    Person loadSingleById(int personId);

    @Query("SELECT * FROM person WHERE dinner_id IS (:dinnerID)")
    List<Person> loadAllByDinner(int dinnerID);

    @Query("SELECT * FROM person WHERE person_couple_id IS (:coupleId)")
    List<Person> loadAllByCouple(int coupleId);

//    @Query("SELECT * FROM dinner WHERE Dinner_Name LIKE :first")
//    Dinner findByName(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Person.class)
    void insert(Person person);

    @Insert(entity = Person.class)
    void insertAll(Person... people);

    @Update(entity = Person.class)
    void updateUsers(Person... people);

    @Delete(entity = Person.class)
    void delete(Person person);

    @Delete(entity = Person.class)
    void deleteByDinner(Person... people);
}
