package com.dparnold.audiovocabulary;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;




public class VocabTest extends AppCompatActivity {
    private final long MILLISDAY = 24*60*60*1000;

    private LinearLayout buttonLayout;
    private Button showButton;
    private Button fineButton;
    private Button againButton;
    private TextView displayVocable;
    private LinearLayout.LayoutParams buttonParams;
    private List<Vocable> vocables;
    private int counter = 0;
    private int studyNumber =30;
    private AppDatabase db;
    private ProgressBar leftProgress;
    private ProgressBar rightProgress;
    private Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_test);
        db = AppDatabase.getAppDatabase(this);

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
        vocables=db.vocableDAO().getMostRelevant(studyNumber);

        // Shuffling the list
        Collections.shuffle(vocables);
        displayVocable.setText(vocables.get(counter).getLang0());

        // Get progress bars, set the color, set progress
        leftProgress = findViewById(R.id.progressBarLeft);
        rightProgress = findViewById(R.id.progressBarRight);
        leftProgress.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        rightProgress.getProgressDrawable().setColorFilter(
                Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        rightProgress.setProgress(vocables.get(counter).getScore()*20);
        leftProgress.setProgress(vocables.get(counter).getScore()*-20);

    }

    public void show(View view){

        displayVocable.setText(vocables.get(counter).getLang1());

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

    public void fine(View view){
        displayVocable.setText(vocables.get(counter).getLang0());
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
        vocables.get(counter).setToStudy(false);

        vocables.get(counter).setLearnNextTime(timestamp.getTime()+studyInterval);
        db.vocableDAO().updateVocable(vocables.get(counter));
        buttonLayout.removeAllViews();
        buttonLayout.addView(showButton);

        counter+=1;
        if(counter >= studyNumber){
            Toast.makeText(VocabTest.this,"Well done!",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void again(View view){
        displayVocable.setText(vocables.get(counter).getLang0());
        vocables.get(counter).setScore(Math.max(vocables.get(counter).getScore()-1,0));
        db.vocableDAO().updateVocable(vocables.get(counter));
        buttonLayout.removeAllViews();
        buttonLayout.addView(showButton);

        counter+=1;
        if(counter >= studyNumber){
            Toast.makeText(VocabTest.this,"Well done!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
