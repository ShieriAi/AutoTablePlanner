package com.example.projecr7.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Dinner.class, Person.class, Table.class, Couple.class, Family.class, Proximity.class, Bribe.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DinnerDao dinnerDao();
    public abstract PersonDao personDao();
    public abstract TableDao tableDao();
    public abstract CoupleDao coupleDao();
    public abstract FamilyDao familyDao();
    public abstract ProximityDao proximityDao();
    public abstract BribeDao bribeDao();

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE dinner ADD COLUMN dinner_score DOUBLE NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE dinner ADD COLUMN dinner_plan INTEGER NOT NULL DEFAULT 0");
        }
    };
}
