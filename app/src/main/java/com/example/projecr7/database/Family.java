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
        indices = {@Index(value = {"family_uid"}, unique = true), @Index(value = {"dinner_id"}), @Index(value = "family_size")})
public class Family {

    @PrimaryKey
    @ColumnInfo(name = "family_uid")
    public int uid;

    @ColumnInfo(name = "dinner_id") public int dinnerId;

    @ColumnInfo(name = "family_size") public int familySize;

    public String displayName;

    public int listPosition;

    public int disLikeBy;

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
        this.disLikeBy = 0;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void increaseDisLikeBy() {
        this.disLikeBy++;
    }

    public int getDisLikeBy() {
        return disLikeBy;
    }

    public String getDisplayName() {
        return displayName;
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