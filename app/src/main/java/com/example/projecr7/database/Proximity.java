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
        indices = {@Index(value = {"proximity_uid"}, unique = true), @Index(value = {"dinner_id"}), @Index(value = {"guest1_Id"}), @Index(value = {"guest2_Id"}), @Index(value = {"proximity_type"})})
public class Proximity {
    @PrimaryKey
    @ColumnInfo(name = "proximity_uid")
    public int uid;

    @ColumnInfo(name = "dinner_id")
    public int dinnerId;

    // 1 for single, 2 for couple, 3 for family
    public int type1;

    public int type2;

    @ColumnInfo(name = "guest1_Id")
    public int guest1Id;

    @ColumnInfo(name = "guest2_Id")
    public int guest2Id;

    // 1 - 5, bad - good
    @ColumnInfo(name = "proximity_type")
    public int proximityType;

    public String guest1String;

    public String guest2String;

    public Proximity(int dinnerId, int type1, int type2, int guest1Id, int guest2Id, int proximityType){
        this.dinnerId = dinnerId;
        this.type1 = type1;
        this.type2 = type2;
        this.guest1Id = guest1Id;
        this.guest2Id = guest2Id;
        this.proximityType = proximityType;
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = month + date + hour + minute + second;
        this.uid = Integer.parseInt(currentT);
    }

    public int getUid() {
        return uid;
    }

    public int getGuest1Id() {
        return guest1Id;
    }

    public int getGuest2Id() {
        return guest2Id;
    }

    public int getType1() {
        return type1;
    }

    public int getType2() {
        return type2;
    }

    public int getProximityType() {
        return proximityType;
    }

    public String getGuest1String() {
        return guest1String;
    }

    public String getGuest2String() {
        return guest2String;
    }

    public void setGuest1String(String guest1String) {
        this.guest1String = guest1String;
    }

    public void setGuest2String(String guest2String) {
        this.guest2String = guest2String;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
