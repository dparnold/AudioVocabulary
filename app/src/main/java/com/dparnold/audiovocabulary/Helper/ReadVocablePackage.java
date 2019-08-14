package com.dparnold.audiovocabulary.Helper;

import android.content.Context;

import com.dparnold.audiovocabulary.Vocable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 2/1/18.
 */

public class ReadVocablePackage {
    Context context;
    int fileName;
    List<Vocable>vocablePackage = new ArrayList<>();

    public ReadVocablePackage(Context context, int fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public List<Vocable> fromTextFile(String packageOrigin, String packageName) throws IOException {
        InputStream is = context.getResources().openRawResource(fileName);//context.getAssets().open(fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        String csvSplitBy = ":";

        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] row = line.split(csvSplitBy);
            Vocable vocable = new Vocable(packageOrigin, packageName,row[1],row[2]);
           vocablePackage.add(vocable);
        }
        return vocablePackage;
    }
    public static List<Vocable> fromStringList(String packageOrigin, String packageName, List<String> stringList){
        List<Vocable>vocablePackage = new ArrayList<>();
        int i=0;
        while (i < stringList.size()){
            vocablePackage.add(new Vocable(packageOrigin, packageName, stringList.get(i+1),stringList.get(i)));
            i+=2;
        }
        return vocablePackage;
    }
}