package com.example.projecr7.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity
public class Dinner {
    @PrimaryKey
    public int uid;

    private String dinnerName;

    public int dinnerYear, dinnerMonth, dinnerDate;

    @ColumnInfo(name = "dinner_score")
    public double score;

    @ColumnInfo(name = "dinner_plan")
    public boolean havePlan;

    public Dinner(String dinnerName){
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = month + date + hour + minute + second;
        this.uid = Integer.parseInt(currentT);
        this.dinnerName = dinnerName;
        this.havePlan = false;
    }

    public void setDinnerName(String givenName){
        dinnerName = givenName;
    }

    public void setUid(int givenUid){
        uid = givenUid;
    }

    public void setDinnerDate(int dinnerYear, int dinnerMonth, int dinnerDate) {
        this.dinnerYear = dinnerYear;
        this.dinnerMonth = dinnerMonth;
        this.dinnerDate = dinnerDate;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setHavePlan(boolean havePlan) {
        this.havePlan = havePlan;
    }

    public boolean isHavePlan() {
        return havePlan;
    }

    public double getScore() {
        return score;
    }

    public int getDinnerYear() {
        return dinnerYear;
    }

    public int getDinnerMonth() {
        return dinnerMonth;
    }

    public int getDinnerDate() {
        return dinnerDate;
    }

    public int getUid() {
        return uid;
    }

    public String getDinnerName() {
        return dinnerName;
    }

    public String toString(){
        return dinnerName;
    }
}
