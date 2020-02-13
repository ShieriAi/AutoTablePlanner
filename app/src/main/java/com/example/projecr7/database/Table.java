package com.example.projecr7.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(onDelete = CASCADE,
        entity = Dinner.class,
        parentColumns = "uid",
        childColumns = "dinner_id"),
        indices = {@Index(value = {"table_id"}, unique = true), @Index(value = {"dinner_id"})})
public class Table {

    @PrimaryKey
    @ColumnInfo(name = "table_id")
    public int uid;

    @ColumnInfo(name = "dinner_id") public int dinnerId;

    public int size;
    //public Person[] peopleList;
    public double score;
    public String tableName;

    public Table(String tableName, int size){
        this.tableName = tableName;
        this.size = size;
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentT = "1" + month + date + hour + minute + second;
        this.uid = Integer.parseInt(currentT);
        score = 0;
        //peopleList = new Person[size];
    }


    public void setTableSize(int givenSize){
        size = givenSize;
    }

    public void setDinnerId(int dinnerId){
        this.dinnerId = dinnerId;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public void setScore(double score){
        this.score = score;
    }

    public int getDinnerId(){
        return dinnerId;
    }

    public int getUid(){
        return uid;
    }

//    public boolean addPerson(Person givenPerson){
//        boolean insert = false;
//        int count = 0;
//        while(!insert && count < size){
//            if(peopleList[count] == null){
//                peopleList[count] = givenPerson;
//                insert = true;
//            }
//            count++;
//        }
//        return insert;
//    }

//    public boolean replacePerson(Person givenPerson, int givenIndex){
//        if(givenIndex >= size){
//            return false;
//        }
//        peopleList[givenIndex] = givenPerson;
//        return true;
//    }
//
//    public boolean removePerson(int index){
//        if(index >= size){
//            return false;
//        }
//        peopleList[index] = null;
//        return true;
//    }
//
//    public boolean swap(int index1, int index2){
//        if(index1 >= size || index2 >= size || index1 == index2){
//            return false;
//        }
//        Person personBr = peopleList[index1];
//        peopleList[index1] = peopleList[index2];
//        peopleList[index2] = personBr;
//        //[peopleList[index1], peopleList[index2]] = [peopleList[index2], peopleList[index1]];
//        return true;
//    }
//
//    public double calculateScore(int[][] relationTable){
//        score = 0;
//        int currentID = 0;
//        int currentPersonHappiness;
//        for(int i = 0; i < 2; i++){
//            currentID = peopleList[i].getId();
//            currentPersonHappiness = 0;
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[(i + size - 2) % size].getId()] / 4.0;
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[(i + size - 1) % size].getId()];
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i + 1].getId()];
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i + 2].getId()] / 4.0;
//            peopleList[i].setHappiness(currentPersonHappiness);
//            score += currentPersonHappiness;
//        }
//        for(int i = 2; i < size - 2; i++){
//            currentID = peopleList[i].getId();
//            currentPersonHappiness = 0;
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i - 2].getId()] / 4.0;
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i - 1].getId()];
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i + 1].getId()];
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i + 2].getId()] / 4.0;
//            peopleList[i].setHappiness(currentPersonHappiness);
//            score += currentPersonHappiness;
//        }
//        for(int i = size - 2; i < size; i++){
//            currentID = peopleList[i].getId();
//            currentPersonHappiness = 0;
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i - 2].getId()] / 4.0;
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[i - 1].getId()];
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[(i + 1) % size].getId()];
//            currentPersonHappiness += (double)relationTable[currentID][peopleList[(i + 2) % size].getId()] / 4.0;
//            peopleList[i].setHappiness(currentPersonHappiness);
//            score += currentPersonHappiness;
//        }
//        return score;
//    }

    public double getScore(){
        return score;
    }

    public int getTableSize(){
        return size;
    }

//    public Person[] getList(){
//        return peopleList;
//    }

    public String getTableName(){
        return tableName;
    }

//    public int getPeopleCount(){
//        return peopleList.length;
//    }
//
//    public Person getPersonIndex(int index){
//        return peopleList[index];
//    }
//
//    public Object clone()throws CloneNotSupportedException{
//        return super.clone();
//    }

    public String toString(){
        String s = tableName + "   Size: " + size;
        return s;
    }


}
