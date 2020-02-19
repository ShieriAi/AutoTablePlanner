package com.example.projecr7.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Dinner.class, Person.class, Table.class, Couple.class, Family.class, Proximity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DinnerDao dinnerDao();
    public abstract PersonDao personDao();
    public abstract TableDao tableDao();
    public abstract CoupleDao coupleDao();
    public abstract FamilyDao familyDao();
    public abstract ProximityDao proximityDao();
}
