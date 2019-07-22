package com.dparnold.audiovocabulary.Helper;
import java.io.*;
import java.net.*; // for .openStream
import java.util.ArrayList;
import android.os.AsyncTask;

public class WebData {
    private static String getHTML(final String URL) {
        String line, all="";
        URL myUrl;
        BufferedReader in = null;
        try {
            myUrl = new URL(URL);
            in = new BufferedReader(new InputStreamReader(myUrl.openStream()));

            while ((line = in.readLine()) != null) {
                all += line;
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        finally {
            if (in != null) {
                //in.close();
            }
        }
        return all;
    }
    public static ArrayList<String> getVocabulary(String URL){
        // Taking the vocabulary into the beginning of the line as it always follows the end of a tag:
        String[] lines = WebData.getHTML(URL).split(">");
        ArrayList<String> resultingVocabulary= new ArrayList<>();
        boolean recording = false; // Shows whether the relevant data started to be recorded
        for (int i =0; i<lines.length;i++){
            lines[i]=lines[i].split("<")[0]; // this removes the tags after the vocabulary
            if(recording && lines[i].split(" ")[0].equals("SpanishDict")){recording =false;} // cue after the last word
            // removing empty and wrong lines:
            if (recording && !lines[i].isEmpty() && !lines[i].equals("Exclude this word from all quizzes and notifications.")){
                resultingVocabulary.add(lines[i]);
                //System.out.println(lines[i]);
            }
            if (lines[i].equals("Quiz")){recording = true;} // this is at the end to avoid recording the keyword
        }
        //resultingVocabulary.remove(resultingVocabulary.size()-1);
        return resultingVocabulary;
    }
}
