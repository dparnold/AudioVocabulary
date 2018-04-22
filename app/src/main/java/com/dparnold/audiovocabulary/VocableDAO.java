package com.dparnold.audiovocabulary;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;



@Dao
public interface VocableDAO {
    @Query("SELECT * FROM vocable")
    List<Vocable> getAll();

    @Query("SELECT * FROM vocable WHERE ID IN (:IDs)")
    List<Vocable> loadAllByIds(int[] IDs);

    // Needs to be split up in getTestVocables and getAudioVocables
    @Query("SELECT * FROM vocable WHERE toTest = 1 ORDER BY score DESC, timesStudied DESC LIMIT (:number)")
    List<Vocable> getMostRelevant(int number);

    @Query("UPDATE vocable SET toTest = 1 WHERE learnNextTime < (:timestamp) AND score <6")
    void updateDue(long timestamp);

    @Insert
    void insertAll(List<Vocable> vocables); //Vocable...

    @Delete
    void delete(Vocable vocable);

    @Update
    void updateVocable(Vocable vocable);
}

