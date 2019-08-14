package com.dparnold.audiovocabulary;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Vocable {
    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name = "packageOrigin")
    private String packageOrigin;

    @ColumnInfo(name = "packageName")
    private String packageName;

    @ColumnInfo(name = "langKnown")
    private String langKnown;

    @ColumnInfo(name = "langForeign")
    private String langForeign;

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

    public Vocable(String packageOrigin, String packageName, String langKnown, String langForeign){
        this.langKnown = langKnown;
        this.langForeign = langForeign;
        this.packageOrigin = packageOrigin;
        this.packageName = packageName;
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

    public String getLangKnown() {
        return langKnown;
    }

    public void setLangKnown(String langKnown) {
        this.langKnown = langKnown;
    }

    public String getLangForeign() {
        return langForeign;
    }

    public void setLangForeign(String langForeign) {
        this.langForeign = langForeign;
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

    public String getPackageOrigin() {
        return packageOrigin;
    }

    public void setPackageOrigin(String packageOrigin) {
        this.packageOrigin = packageOrigin;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
