package com.dparnold.audiovocabulary.helper;

import android.content.Context;
import android.content.res.Resources;

import com.dparnold.audiovocabulary.Vocable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 2/1/18.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadVocablePackage {
    Context context;
    int fileName;
    List<Vocable>vocablePackage = new ArrayList<>();

    public ReadVocablePackage(Context context, int fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public List<Vocable> read() throws IOException {
        InputStream is = context.getResources().openRawResource(fileName);//context.getAssets().open(fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        String csvSplitBy = ":";

        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] row = line.split(csvSplitBy);
            Vocable vocable = new Vocable(Integer.parseInt(row[0]),row[1],row[2]);
           vocablePackage.add(vocable);
        }
        return vocablePackage;
    }
}