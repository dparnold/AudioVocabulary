package com.dparnold.audiovocabulary;

import android.Manifest;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.dparnold.audiovocabulary.Helper.SpanishDict;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

// Still hardcoded
    private int numberOfVocablesToStudy = 30;
    private String currentVocablePackage = "344204/verbs";
    private int sleepTimerMinutes = 20;

    private ImageButton playButton,keepScreenOnButton ,sleepTimerButton;
    private boolean playing = false;
    private boolean keepScreenOn = false;
    private boolean sleepTimerOn = false;
    private boolean starting = true;
    private boolean firstStart = true;
    private MediaPlayer mediaPlayer;
    private com.dparnold.audiovocabulary.AppDatabase db;
    private Handler playHandler = new Handler();
    private Handler sleepHander = new Handler();
    private Runnable firstRunnable, secondRunnable, sleepRunnable;
    private int vocablesIndex = 0;
    private int delayBetweenVocables;
    private List<Vocable> vocables;
    private TextView textViewLangKnown, textViewLangForeign;
    private Timestamp timestamp;
    private SharedPreferences settings; // The Preferences object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Requesting Permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1); // request code can be arbitrary
            }


        keepScreenOnButton = findViewById(R.id.wakeLockButton);
        keepScreenOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleKeepScreenOn(v);
            }
        });


        settings = getSharedPreferences(Settings.SETTINGS_NAME, 0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        getSettings();


        // Getting a timestamp for the current session
        timestamp = new Timestamp(System.currentTimeMillis());



        db = com.dparnold.audiovocabulary.AppDatabase.getAppDatabase(this);
        //db.vocableDAO().nukeTable();





        if(firstStart){
            SpanishDict newDict =new SpanishDict();
            newDict.addVocabularyToDatabase(db,"https://www.spanishdict.com/lists/344204/verbs");
            SpanishDict newDict2 =new SpanishDict();
            newDict2.addVocabularyToDatabase(db,"https://www.spanishdict.com/lists/334717/body-people-house");
        }
        else{
            vocables=db.vocableDAO().getMostRelevant(numberOfVocablesToStudy, currentVocablePackage);  // Getting the vocables from the database
            // Shuffling the list
            Collections.shuffle(vocables);
            // If that fails, import the data to the database
            db.vocableDAO().updateDue(timestamp.getTime());  // Checking for vocabulary that is due
        }

        textViewLangKnown = findViewById(R.id.textViewlangKnown);
        textViewLangForeign = findViewById(R.id.textViewlangForeign);
        textViewLangKnown.setText("Welcome!");
        textViewLangForeign.setText("¡Bienvenidos!");
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
        settingsEditor.putBoolean("firstStart", false);
        settingsEditor.commit();
    }
    public void toVocabTest(View view){
        if(playing){
            stopPlayback();
        }
        startActivity(new Intent(MainActivity.this, com.dparnold.audiovocabulary.VocabTest.class));
    }
    public void toSettings (View view){
        if(playing){
            stopPlayback();
        }
        startActivity(new Intent(MainActivity.this, Settings.class));
    }
    public void toVocablePackages (View view){
        startActivity(new Intent(MainActivity.this, VocablePackages.class));
    }
    public void turnOff(View view){
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
    public void toVocabList (View view){
        if(playing){
            stopPlayback();
        }
        startActivity(new Intent(MainActivity.this, com.dparnold.audiovocabulary.VocableList.class));
    }
    public void toTest1 (View view){
        startActivity(new Intent(MainActivity.this, FileDownload.class));
    }
    public void toTest2 (View view){
        startActivity(new Intent(MainActivity.this, VocablePackages.class));
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
        vocables=db.vocableDAO().getMostRelevant(numberOfVocablesToStudy, currentVocablePackage);
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
                    textViewLangForeign.setText("");
                    String filePath = Environment.getExternalStorageDirectory()+"/MyAppFolder/"+SpanishDict.convertAudioStrings(vocables.get(vocablesIndex).getLangKnown())+".mp3";
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(filePath);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (playing) {
                                playHandler.postDelayed(secondRunnable, delayBetweenVocables);
                            }
                        }
                    });
                    mediaPlayer.start();
                    textViewLangKnown.setText(vocables.get(vocablesIndex).getLangKnown());
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
                    String filePath = Environment.getExternalStorageDirectory()+"/MyAppFolder/"+SpanishDict.convertAudioStrings(vocables.get(vocablesIndex).getLangForeign())+".mp3";
                    mediaPlayer = new  MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(filePath);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    textViewLangForeign.setText(vocables.get(vocablesIndex).getLangForeign());
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
                            textViewLangKnown.setText("");
                            playHandler.postDelayed(firstRunnable, delayBetweenVocables);

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
        delayBetweenVocables = (int) settings.getFloat("delayBetweenVocables",(float)1.0)*1000;
        numberOfVocablesToStudy = settings.getInt("vocablesNumber", 30);
        if (settings.getBoolean("screenOn",false)!= keepScreenOn){
            toggleKeepScreenOn(keepScreenOnButton);
        }
        currentVocablePackage = settings.getString("packageName", currentVocablePackage);
        firstStart = settings.getBoolean("firstStart",true);
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
                sleepTimerMinutes = sleepNumberPicker.getValue()*5;
                Log.e("info", Integer.toString(sleepTimerMinutes));

                sleepRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        System.exit(0);
                    }
                };
                sleepHander.postDelayed(sleepRunnable, 1000*60* sleepTimerMinutes);
                alertDialog.dismiss();

            }
        });
       alertDialog.show();
    }
    void toggleKeepScreenOn(View view){
        keepScreenOn = !keepScreenOn;
        if(keepScreenOn){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View root = findViewById(android.R.id.content);
            if (root != null){
                root.setKeepScreenOn(true);
            }
            if(starting != true){
                Toast.makeText(getApplicationContext(),"Screen will not be locked",Toast.LENGTH_SHORT).show();}
            keepScreenOnButton.setImageResource(R.drawable.ic_unlock);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View root = findViewById(android.R.id.content);
            if (root != null){
                root.setKeepScreenOn(false);}
            Toast.makeText(getApplicationContext(),"Screen will lock automatically", Toast.LENGTH_SHORT).show();
            keepScreenOnButton.setImageResource(R.drawable.ic_lock);
        }
    }
    private void stopPlayback(){
        playing = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        textViewLangKnown.setText("");
        textViewLangForeign.setText("");
        playButton.setImageResource(R.drawable.ic_play);
    }
}
