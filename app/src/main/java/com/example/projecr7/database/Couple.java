package com.example.projecr7.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(onDelete = CASCADE,
        entity = Dinner.class,
        parentColumns = "uid",
        childColumns = "dinner_id"),
        indices = {@Index(value = {"couple_uid"}, unique = true), @Index(value = {"dinner_id"})})
public class Couple {

    @PrimaryKey
    @ColumnInfo(name = "couple_uid")
    public int uid;

    @ColumnInfo(name = "dinner_id") public int dinnerId;

    public String displayName;

    public Couple(){
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = "1" + month + date + hour + minute + second;
        this.uid = Integer.parseInt(currentT);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public void setDinnerId(int dinnerId) {
        this.dinnerId = dinnerId;
    }

    public int getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDinnerId(){
        return  dinnerId;
    }
}
