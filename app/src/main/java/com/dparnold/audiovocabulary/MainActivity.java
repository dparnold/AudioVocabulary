package com.dparnold.audiovocabulary;

import android.content.SharedPreferences;
import android.os.Handler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private boolean playing = false;
    private MediaPlayer mediaPlayer;
    private com.dparnold.audiovocabulary.AppDatabase db;
    private Handler playHandler = new Handler();
    private Runnable firstRunnable;
    private Runnable secondRunnable;
    private int vocablesIndex = 0;
    private List<Vocable> vocables;
    private TextView textViewLang0;
    private TextView textViewLang1;

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


}
