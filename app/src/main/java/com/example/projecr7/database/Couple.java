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
    public long uid;

    @ColumnInfo(name = "dinner_id") public int dinnerId;

    public String displayName;

    public int listPosition;

    public int disLikeBy;

    public Couple(){
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = "1" + month + date + hour + minute + second;
        this.uid = Long.parseLong(currentT);
        this.disLikeBy = 0;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUid(long uid){
        this.uid = uid;
    }

    public void setDinnerId(int dinnerId) {
        this.dinnerId = dinnerId;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public void increaseDisLikeBy() {
        this.disLikeBy++;
    }

    public int getDisLikeBy() {
        return disLikeBy;
    }

    public int getListPosition() {
        return listPosition;
    }

    public long getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDinnerId(){
        return  dinnerId;
    }
}
