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
        indices = {@Index(value = {"family_uid"}, unique = true), @Index(value = {"dinner_id"})})
public class Family {

    @PrimaryKey
    @ColumnInfo(name = "family_uid")
    public int uid;

    @ColumnInfo(name = "dinner_id") public int dinnerId;

    public int familySize;

    public Family(int familySize){
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = "4" + month + date + hour + minute + second.charAt(0);
        this.uid = Integer.parseInt(currentT);
        this.familySize = familySize;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public int getFamilySize() {
        return familySize;
    }

    public void setDinnerId(int dinnerId) {
        this.dinnerId = dinnerId;
    }

    public int getUid() {
        return uid;
    }

    public int getDinnerId(){
        return  dinnerId;
    }
}