package com.dparnold.audiovocabulary.Helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dparnold.audiovocabulary.AppDatabase;
import com.dparnold.audiovocabulary.FileDownload;
import com.dparnold.audiovocabulary.MainActivity;
import com.dparnold.audiovocabulary.Vocable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SpanishDict {
    AppDatabase db;
    private String url;
    private String packageName;
    private String audioDownloadString = "";
    final String audioForeignUrl = "https://audio1.spanishdict.com/audio?lang=es&text=";
    final String audioKnownUrl = "https://audio1.spanishdict.com/audio?lang=en&text=";
    public void addVocabularyToDatabase (AppDatabase db, String url){
        this.db = db;
        this.url = url;
        this.packageName =getPackageNameFromURl(url);
        new DownloadWebTask().execute(url);
    }
    public static ArrayList<String> downloadVocabularyArray(String URL){
        // Taking the vocabulary into the beginning of the line as it always follows the end of a tag:
        String[] lines = getHTML(URL).split(">");
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
    private class DownloadAudioTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urlParams) {
            int count;
            try {
                URL url = new URL(urlParams[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();
                if(lenghtOfFile==0){
                    Log.d("MissingAudioFile",urlParams[0]+urlParams[1]);
                }
                Log.d("Downloading",urlParams[0]+urlParams[1]);
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());

                String filePath= Environment.getExternalStorageDirectory()+"/MyAppFolder/"+urlParams[1];
                OutputStream output = new FileOutputStream(filePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }
    }
    private class DownloadWebTask extends AsyncTask<String, Void, Void> {
        public void DownloadWebTask (){
        // empty constructor
        }
        protected Void doInBackground(String ... Void) {
            ArrayList<String> vocableList = downloadVocabularyArray(url);
            db.vocableDAO().insertAll(ReadVocablePackage.fromStringList("SpanishDict", packageName ,vocableList));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<Vocable> vocabulary= db.vocableDAO().getAll();
            for (int i =0; i < vocabulary.size();i++){
                audioDownloadString=convertAudioStrings(vocabulary.get(i).getLangForeign());
                new DownloadAudioTask().execute(audioForeignUrl+audioDownloadString, audioDownloadString+".mp3");
                audioDownloadString=convertAudioStrings(vocabulary.get(i).getLangKnown());
                new DownloadAudioTask().execute(audioKnownUrl+audioDownloadString, audioDownloadString+".mp3");
            }
        }
    }
    public static String convertAudioStrings(String inputString){
        String outputString = inputString.replace(' ', '-');
        outputString=outputString.replace("el-","");
        outputString=outputString.replace("la-","");
        return outputString;
    }
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
    private String getPackageNameFromURl (String url){
        // Input: https://www.spanishdict.com/lists/344204/verbs
        String[] resultList = url.split("/");
        // Output: 344204/verbs
        return resultList[resultList.length-2]+"/"+resultList[resultList.length-1];
    }
}
