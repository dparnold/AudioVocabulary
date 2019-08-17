package com.dparnold.audiovocabulary;


import android.content.SharedPreferences;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static android.media.CamcorderProfile.get;


public class VocabTest extends AppCompatActivity {
    private final long MILLISDAY = 24*60*60*1000;
    private String currentVocablePackage;

    private LinearLayout buttonLayout;
    private Button showButton;
    private Button fineButton;
    private Button againButton;
    private TextView displayVocable;
    private LinearLayout.LayoutParams buttonParams;
    private List<Vocable> vocables;
    private int counter = 0;
    private int studyNumber =20;
    private int maxScore = 6;
    private AppDatabase db;
    private ProgressBar rightProgress;
    private Timestamp timestamp;
    private Toast finishedToast;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_test);
        db = AppDatabase.getAppDatabase(this);

        settings = getSharedPreferences(Settings.SETTINGS_NAME, 0);
        currentVocablePackage = settings.getString("currentVocablePackage", db.vocableDAO().getPackageNames().get(0));


        // Getting a timestamp for the current session
        timestamp = new Timestamp(System.currentTimeMillis());

        buttonLayout = findViewById(R.id.buttonLayout);
        displayVocable = findViewById(R.id.displayVocable);
        buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);


        showButton = new Button(this);
        showButton.setLayoutParams(buttonParams);
        showButton.setText("show");
        showButton.setTextSize(20);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(showButton);
            }
        });
        buttonLayout.addView(showButton);

        // Getting vocabulary
        vocables=db.vocableDAO().getMostRelevant(studyNumber,currentVocablePackage);

        // Shuffling the list
        Collections.shuffle(vocables);
        displayVocable.setText(vocables.get(counter).getLangKnown());

        // Get progress bars, set the color, set progress
        rightProgress = findViewById(R.id.progressBarRight);
        rightProgress.getProgressDrawable().setColorFilter(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, null), android.graphics.PorterDuff.Mode.SRC_IN);
        int progress = (int) ((vocables.get(counter).getScore()==0) ? 2: 100*vocables.get(counter).getScore()/maxScore);
        rightProgress.setProgress(progress);

        // Setting the Toast messages for when the test is finished
        finishedToast = Toast.makeText(VocabTest.this,"Well done!",Toast.LENGTH_SHORT);
        finishedToast.setGravity(Gravity.CENTER,0, 0);


    }

    public void show(View view){

        displayVocable.setText(vocables.get(counter).getLangForeign());

        buttonLayout.removeAllViews();

        againButton = new Button(this);
        againButton.setLayoutParams(buttonParams);
        againButton.setText("again");
        againButton.setTextSize(20);
        againButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                again(againButton);
            }
        });

        fineButton = new Button(this);
        fineButton.setLayoutParams(buttonParams);
        fineButton.setText("fine");
        fineButton.setTextSize(20);
        fineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               fine(fineButton);
            }
        });

        buttonLayout.addView(againButton);
        buttonLayout.addView(fineButton);

        vocables.get(counter).setTimesStudied(vocables.get(counter).getTimesStudied()+1);
        db.vocableDAO().updateVocable(vocables.get(counter));

    }
    public void ignore (View view){
        vocables.get(counter).setScore(maxScore);
        vocables.get(counter).setToTest(false);
        db.vocableDAO().updateVocable(vocables.get(counter));

        counter+=1;
        if(counter >= studyNumber){
            finishedToast.show();
            finish();
        }
        else{
            displayVocable.setText(vocables.get(counter).getLangKnown());
        }
    }
    public void fine(View view){
        int score = vocables.get(counter).getScore();
        vocables.get(counter).setScore(score+1);

        // Set the study interval for the differnt levels
        long studyInterval;
        switch (score){
            case 0: studyInterval = MILLISDAY; // Milliseconds of one day
                break;
            case 1: studyInterval = 2*MILLISDAY; // Two days
                break;
            case 2: studyInterval = 5*MILLISDAY;
                break;
            case 3: studyInterval =15*MILLISDAY;
                break;
            case 4: studyInterval = 30*MILLISDAY;
                break;
            case 5: studyInterval = 90*MILLISDAY;
                break;
            case 6: studyInterval = 200*MILLISDAY;
                break;
            default:studyInterval = 0;
                break;
        }
        // Prevent this vocable from showing up in the next test
        vocables.get(counter).setToTest(false);
        // Remove the vocable from the ones to listen to
        vocables.get(counter).setToListen(false);
        // Set the time of the next studying 
        vocables.get(counter).setLearnNextTime(timestamp.getTime()+studyInterval);
        // Update vocable
        db.vocableDAO().updateVocable(vocables.get(counter));
        buttonLayout.removeAllViews();
        buttonLayout.addView(showButton);

        counter+=1;
        if(counter >= studyNumber){
            finishedToast.show();
            finish();
        }
        else{
            displayVocable.setText(vocables.get(counter).getLangKnown());
        }

    }

    public void again(View view){
        // Prevent this vocable from showing up in the next test
        vocables.get(counter).setToTest(false);
        // Reduce the score by 1 or keep it at 0
        vocables.get(counter).setScore(Math.max(vocables.get(counter).getScore()-1,0));
        // Vocable will appear in the next days test
        vocables.get(counter).setLearnNextTime(timestamp.getTime()+MILLISDAY);
        // The user has to listen to the vocable
        vocables.get(counter).setToListen(true);
        // Update the vocable
        db.vocableDAO().updateVocable(vocables.get(counter));
        buttonLayout.removeAllViews();
        buttonLayout.addView(showButton);

        counter+=1;
        if(counter >= studyNumber){
            finishedToast.show();
            finish();
        }
        else {
            displayVocable.setText(vocables.get(counter).getLangKnown());
        }
    }
}
