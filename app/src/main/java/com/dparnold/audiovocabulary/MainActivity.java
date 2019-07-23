package com.dparnold.audiovocabulary;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.dparnold.audiovocabulary.Helper.ReadVocablePackage;
import com.dparnold.audiovocabulary.Helper.Util;
import com.dparnold.audiovocabulary.Helper.WebData;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    int mostRelevant= 30;

    // File where the settings are saved.
    public static final String SETTINGS_NAME = "AppSettings";
    // The Preferences object
    private SharedPreferences settings;

    private int fileNumber = 1;
    private ImageButton playButton;
    private ImageButton wakeLockButton;
    private ImageButton sleepTimerButton;
    private boolean playing = false;
    private boolean wakeLock = false;
    private boolean sleepTimerOn = false;
    private MediaPlayer mediaPlayer;
    private com.dparnold.audiovocabulary.AppDatabase db;
    private Handler playHandler = new Handler();
    private Handler sleepHander = new Handler();
    private Runnable firstRunnable;
    private Runnable secondRunnable;
    private int vocablesIndex = 0;
    private List<Vocable> vocables;
    private TextView textViewLang0;
    private TextView textViewLang1;
    private int sleepDelay = 20;
    private Timestamp timestamp;
    private Runnable sleepRunnable;

    int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting a timestamp for the current session
        timestamp = new Timestamp(System.currentTimeMillis());

        // Get settings
        getSettings();

        db = com.dparnold.audiovocabulary.AppDatabase.getAppDatabase(this);
        db.vocableDAO().nukeTable();
        WebData.downloadFile(getApplicationContext());//############################################################
        // Checking for vocabulary that is due
        db.vocableDAO().updateDue(timestamp.getTime());
        // Getting the vocables from the database
        vocables=db.vocableDAO().getMostRelevant(mostRelevant);
        // Shuffling the list
        Collections.shuffle(vocables);
        // If that fails, import the data to the database
        if (vocables.isEmpty()) {
            ReadVocablePackage csvReader = new ReadVocablePackage(this, R.raw.package1);
            try {
                vocables = csvReader.fromTextFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //#################
            new DownloadWebTask().execute("https://www.spanishdict.com/lists/334717/body-kitchen");
            Log.i("Info:","Data fromTextFile from the package file.");
        }
        else {Log.i("Info:","Data successfully loaded from the database."); }// Info

        textViewLang0 = findViewById(R.id.textViewLang0);
        textViewLang1 = findViewById(R.id.textViewLang1);
        textViewLang0.setText("Welcome!");
        textViewLang1.setText("¡Bienvenidos!");

        wakeLockButton = findViewById(R.id.wakeLockButton);
        wakeLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWakeLock(v);
            }
        });

        sleepTimerButton = findViewById(R.id.sleepTimerButton);

        playButton=findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playing = !playing;
                if (playing){
                    playButton.setImageResource(R.drawable.ic_stop);
                    play();
                }

                else{
                    stopPlayback();
                }
            }
        });
    }
    public void toVocabTest(View view){
        if(playing){
            stopPlayback();
        }
        startActivity(new Intent(MainActivity.this, com.dparnold.audiovocabulary.VocabTest.class));
    }
    public void toSettings (View view){
        startActivity(new Intent(MainActivity.this, Settings.class));
    }
    public void toVocabList (View view){
        if(playing){
            stopPlayback();
        }
        startActivity(new Intent(MainActivity.this, com.dparnold.audiovocabulary.VocableList.class));
    }
    public void toTest1 (View view){
        startActivity(new Intent(MainActivity.this, Settings.class));
    }
    public void toTest2 (View view){
        startActivity(new Intent(MainActivity.this, Settings.class));
    }
    public void toTest3 (View view){
        startActivity(new Intent(MainActivity.this, Settings.class));
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        // Update the settings
        getSettings();
        // update the most relevant vocables
        vocables=db.vocableDAO().getMostRelevant(mostRelevant);
        // Shuffling the list
        Collections.shuffle(vocables);
        }




    public void play(){

        // This method uses handlers to play the audio files in the right order with the set delays
        // playHandler.postDelayed gives the Runnable and the delay time
        // First the runnables are defined
        firstRunnable = new Runnable() {
            @Override
            public void run() {
                if(playing) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    textViewLang1.setText("");
                    final int resource = MainActivity.this.getResources().getIdentifier("package1_en_"
                                    + Util.int2StringDigits(vocables.get(vocablesIndex).getID(), 3),
                            "raw", "com.dparnold.audiovocabulary");
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resource);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (playing) {
                                playHandler.postDelayed(secondRunnable, delay);
                            }
                        }
                    });
                    mediaPlayer.start();
                    textViewLang0.setText(vocables.get(vocablesIndex).getLangKnown());
                }
            }
        };
        secondRunnable = new Runnable() {
            @Override
            public void run() {
                if(playing) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    final int resource = MainActivity.this.getResources().getIdentifier("package1_es_"
                                    + Util.int2StringDigits(vocables.get(vocablesIndex).getID(), 3),
                            "raw", "com.dparnold.audiovocabulary");
                    mediaPlayer = MediaPlayer.create(MainActivity.this, resource);
                    textViewLang1.setText(vocables.get(vocablesIndex).getLangForeign());
                    // Next vocable in the list
                    if (vocablesIndex == vocables.size() - 1) {
                        vocablesIndex = 0;
                        // Shuffling the list again
                        // !!! can lead to having the same vocable twice in a row
                        Collections.shuffle(vocables);
                    } else {
                        vocablesIndex += 1;
                    }
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {

                            // Starting with the word in the known language again
                            textViewLang0.setText("");
                            playHandler.postDelayed(firstRunnable, delay);

                        }
                    });
                    mediaPlayer.start();
                }
            }
        };
        // Start with the first runnable right away (delay = 0)
        playHandler.postDelayed(firstRunnable,0);
    }

    void getSettings() {
        // 0 signifies the standard operating mode
        settings = getSharedPreferences(SETTINGS_NAME, 0);
        delay = (int) settings.getFloat("delay",(float)1.0)*1000;
    }

    void sleepTimer (View view){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.sleep_timer_dialog, null);
        // Create a new AlerDialog with my custom theme
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme).create();
        // this is set the view from XML inside AlertDialog
        alertDialog.setView(alertLayout);

        Button sleepTimerCancelButton = alertLayout.findViewById(R.id.sleepTimerCancelButton);
        if(sleepTimerOn){
            sleepTimerCancelButton.setText("Stop Timer");
            sleepTimerCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sleepTimerOn = false;
                    sleepTimerButton.setImageResource(R.drawable.ic_sleep);
                    sleepHander.removeCallbacksAndMessages(null);
                    alertDialog.dismiss();
                }
            });
        }
        else{
            sleepTimerCancelButton.setText("Cancel");
            sleepTimerCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }



        final NumberPicker sleepNumberPicker =  alertLayout.findViewById(R.id.sleepNumberPicker);
        sleepNumberPicker.setMinValue(1);
        sleepNumberPicker.setMaxValue(6);
        sleepNumberPicker.setValue(4);
        sleepNumberPicker.setDisplayedValues( new String[] { "5", "10", "15", "20", "25", "30" } );



        Button sleepTimerSetButton = alertLayout.findViewById(R.id.sleepTimerSetButton);
        if(sleepTimerOn){
            sleepTimerSetButton.setText("Reset Timer");
        }
        else{
            sleepTimerSetButton.setText("Set Timer");
        }
        sleepTimerSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleepTimerOn =true;
                sleepTimerButton.setImageResource(R.drawable.ic_sleep_active);
                sleepDelay = sleepNumberPicker.getValue()*5;
                Log.e("info", Integer.toString(sleepDelay));

                sleepRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        System.exit(0);
                    }
                };
                sleepHander.postDelayed(sleepRunnable, 1000*60*sleepDelay);
                alertDialog.dismiss();

            }
        });
       alertDialog.show();
    }
    void toggleWakeLock(View view){
        wakeLock = !wakeLock;
        if(wakeLock){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View root = findViewById(android.R.id.content);
            if (root != null)
                root.setKeepScreenOn(true);
            Toast.makeText(getApplicationContext(),"Screen will not be locked",Toast.LENGTH_SHORT).show();
            wakeLockButton.setImageResource(R.drawable.ic_unlock);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View root = findViewById(android.R.id.content);
            if (root != null)
                root.setKeepScreenOn(false);
            Toast.makeText(getApplicationContext(),"Screen will lock automatically", Toast.LENGTH_SHORT).show();
            wakeLockButton.setImageResource(R.drawable.ic_lock);
        }
    }
    private void stopPlayback(){
        playing = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        textViewLang0.setText("");
        textViewLang1.setText("");
        playButton.setImageResource(R.drawable.ic_play);
    }

    // The downloadin process has to be an async task
    private class DownloadWebTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String ... url) {
            db.vocableDAO().insertAll(ReadVocablePackage.fromStringList(WebData.getVocabulary(url[0])));
            WebData.downloadFile(getApplicationContext());
            return null;
        }
    }
}
