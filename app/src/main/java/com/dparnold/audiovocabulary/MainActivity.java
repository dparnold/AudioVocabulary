package com.dparnold.audiovocabulary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dparnold.audiovocabulary.helper.ReadVocablePackage;
import com.dparnold.audiovocabulary.helper.Util;


import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.dparnold.audiovocabulary.Settings.SETTINGS_NAME;


public class MainActivity extends AppCompatActivity {

    int mostRelevant= 30;

    // File where the settings are saved.
    public static final String SETTINGS_NAME = "AppSettings";
    // The Preferences object
    private SharedPreferences settings;

    private int fileNumber = 1;
    private Button playButton;
    private Button wakeLockButton;
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

    int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get settings
        getSettings();

        db = com.dparnold.audiovocabulary.AppDatabase.getAppDatabase(this);

        // Getting the vocables from the database
        vocables=db.vocableDAO().getMostRelevant(mostRelevant);
        // Shuffling the list
        Collections.shuffle(vocables);
        // If that fails, import the data to the database
        if (vocables.isEmpty()) {
            ReadVocablePackage csvReader = new ReadVocablePackage(this, R.raw.package1);
            try {
                vocables = csvReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            db.vocableDAO().insertAll(vocables);
            Log.i("Info:","Data read from the package file.");
        }
        else {Log.i("Info:","Data successfully loaded from the database."); }// Info

        textViewLang0 = findViewById(R.id.textViewLang0);
        textViewLang1 = findViewById(R.id.textViewLang1);
        textViewLang0.setText("");
        textViewLang1.setText("");

        wakeLockButton = findViewById(R.id.wakeLockButton);
        wakeLockButton.setText("Screen locks automatically");

        wakeLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeLock = !wakeLock;
                if(wakeLock){
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    View root = findViewById(android.R.id.content);
                    if (root != null)
                        root.setKeepScreenOn(true);
                    wakeLockButton.setText("Screen does not lock");
                }
                else{
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    View root = findViewById(android.R.id.content);
                    if (root != null)
                        root.setKeepScreenOn(false);
                    wakeLockButton.setText("Screen locks automatically");
                }
            }
        });

        playButton=findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playing = !playing;
                if (playing){
                    playButton.setText("Stop");
                    play();
                }

                else{
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;



                    textViewLang0.setText("");
                    textViewLang1.setText("");
                    playButton.setText("Play");
                }
            }
        });
    }
    public void toVocabTest(View view){
        startActivity(new Intent(MainActivity.this, com.dparnold.audiovocabulary.VocabTest.class));
    }
    public void toSettings (View view){
        startActivity(new Intent(MainActivity.this, Settings.class));
    }
    public void toVocabList (View view){
        startActivity(new Intent(MainActivity.this, com.dparnold.audiovocabulary.VocableList.class));
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getSettings();

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
                    textViewLang0.setText(vocables.get(vocablesIndex).getLang0());
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
                    textViewLang1.setText(vocables.get(vocablesIndex).getLang1());
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
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Sleep Timer");
        alertDialog.setMessage("Minutes until the app closes:");
        final NumberPicker numberPicker =  (NumberPicker) new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(6);
        numberPicker.setValue(4);
        numberPicker.setDisplayedValues( new String[] { "5", "10", "15", "20", "25", "30" } );
        alertDialog.setView(numberPicker);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Set Timer",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sleepDelay = numberPicker.getValue()*5;
                        Log.e("info", Integer.toString(sleepDelay));
                        sleepTimerOn =true;
                        Runnable sleepRunnable = new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                System.exit(0);
                            }
                        };
                        sleepHander.postDelayed(sleepRunnable, 1000*60*sleepDelay);
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }

}
