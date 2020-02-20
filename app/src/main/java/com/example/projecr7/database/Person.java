package com.example.projecr7.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_DEFAULT;

@Entity(foreignKeys = {
        @ForeignKey(onDelete = CASCADE,
        entity = Dinner.class,
        parentColumns = "uid",
        childColumns = "dinner_id"),
        @ForeignKey(onDelete = SET_DEFAULT,
        entity = Table.class,
        parentColumns = "table_id",
        childColumns = "person_table_id"
        ),
        @ForeignKey(onDelete = CASCADE,
                entity = Couple.class,
                parentColumns = "couple_uid",
                childColumns = "person_couple_id"
        ),@ForeignKey(onDelete = CASCADE,
        entity = Family.class,
        parentColumns = "family_uid",
        childColumns = "person_family_id"
        )},
        indices = {@Index(value = {"person_uid"}, unique = true), @Index(value = {"dinner_id"}), @Index(value = {"person_table_id"}), @Index(value = {"person_couple_id"}), @Index(value = {"person_family_id"})})
public class Person {

    @PrimaryKey
    @ColumnInfo(name = "person_uid")
    public int uid;

    @ColumnInfo(name = "dinner_id")
    public int dinnerId;

    private String name;

    private String gender;
    public double currentHappiness;

    @ColumnInfo(name = "person_table_id", defaultValue = "4")
    public int tableId;

    public int seatId;

    @ColumnInfo(name = "person_couple_id", defaultValue = "4")
    public int coupleId;

    @ColumnInfo(name = "person_family_id", defaultValue = "4")
    public int familyId;

    public Person(String name, String gender){
        this.name = name;
        this.gender = gender;
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = "2" + month + date + hour + minute + second;
        this.uid = Integer.parseInt(currentT);
        this.currentHappiness = 0;
        this.familyId = 4;
        this.coupleId = 4;
        this.tableId = 4;
    }

    public void setOtherId(){
        this.uid++;
    }

    public void setOtherId(int i){
        this.uid += i;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public void setId(int uid){
        this.uid = uid;
    }

    public void setHappiness(double currentHappiness){
        this.currentHappiness = currentHappiness;
    }

    public void setTableId(int tableId){
        this.tableId = tableId;
    }

    public void setDinnerId(int dinnerId){
        this.dinnerId = dinnerId;
    }

    public void setCoupleId(int coupleId){
        this.coupleId = coupleId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public void setSeatId(int seatId){
        this.seatId = seatId;
    }

    public int getSeatId() {
        return seatId;
    }

    public int getCoupleId(){
        return  coupleId;
    }

    public int getFamilyId(){
        return familyId;
    }

    public String getName(){
        return name;
    }

    public String getGender(){
        return gender;
    }

    public int getTableId(){
        return tableId;
    }

    public int getId(){
        return uid;
    }

    public int getDinnerId(){
        return dinnerId;
    }

    public double getHappiness(){
        return currentHappiness;
    }



    public String toString(){
        //String s = "Id: " + uid + ", Name: " + name + ", Gender: " + gender + ", Happiness: " + currentHappiness;
        return name;
    }

}
