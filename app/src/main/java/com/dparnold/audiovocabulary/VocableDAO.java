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

    @Query("SELECT * FROM vocable WHERE score < 3 ORDER BY score DESC, timesStudied DESC LIMIT (:number)")
    List<Vocable> getMostRelevant(int number);

    @Insert
    void insertAll(List<Vocable> vocables); //Vocable...

    @Delete
    void delete(Vocable vocable);

    @Update
    void updateVocable(Vocable vocable);
}

