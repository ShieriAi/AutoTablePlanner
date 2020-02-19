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
        indices = {@Index(value = {"bribe_uid"}, unique = true), @Index(value = {"dinner_id"}), @Index(value = {"guest_Id"})})
public class Bribe {
    @PrimaryKey
    @ColumnInfo(name = "bribe_uid")
    public int uid;

    @ColumnInfo(name = "dinner_id")
    public int dinnerId;

    @ColumnInfo(name = "guest_Id")
    public int guestId;

    public int guestType;

    public String guestName;

    @ColumnInfo(name = "bribe_amount")
    public int bribeAmount;

    public Bribe(int dinnerId, int guestId, int guestType, String guestName, int bribeAmount){
        this.dinnerId = dinnerId;
        this.guestId = guestId;
        this.bribeAmount = bribeAmount;
        this.guestType = guestType;
        this.guestName = guestName;
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

    public int getGuestId() {
        return guestId;
    }

    public int getBribeAmount() {
        return bribeAmount;
    }

    public int getGuestType() {
        return guestType;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setBribeAmount(int bribeAmount) {
        this.bribeAmount = bribeAmount;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
