package com.dparnold.audiovocabulary;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Vocable {
    @PrimaryKey
    private int ID;

    @ColumnInfo(name = "lang0")
    private String lang0;

    @ColumnInfo(name = "lang1")
    private String lang1;

    @ColumnInfo(name = "timesStudied")
    private int timesStudied;

    @ColumnInfo(name= "toTest")
    private boolean toTest = true;

    @ColumnInfo(name= "toListen")
    private boolean toListen = true;

    @ColumnInfo(name = "score")
    private int score;

    @ColumnInfo(name = "learnNextTime")
    private long learnNextTime;


    public Vocable(int ID, String lang0, String lang1){
        this.ID=ID;
        this.lang0=lang0;
        this.lang1=lang1;
    }

    public boolean isToListen() {
        return toListen;
    }

    public void setToListen(boolean toListen) {
        this.toListen = toListen;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLang0() {
        return lang0;
    }

    public void setLang0(String lang0) {
        this.lang0 = lang0;
    }

    public String getLang1() {
        return lang1;
    }

    public void setLang1(String lang1) {
        this.lang1 = lang1;
    }

    public int getTimesStudied() {
        return timesStudied;
    }

    public void setTimesStudied(int timesStudied) {
        this.timesStudied = timesStudied;
    }

    public boolean isToTest() {
        return toTest;
    }

    public void setToTest(boolean toTest) {
        this.toTest = toTest;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getLearnNextTime() {
        return learnNextTime;
    }

    public void setLearnNextTime(long learnNextTime) {
        this.learnNextTime = learnNextTime;
    }


}
