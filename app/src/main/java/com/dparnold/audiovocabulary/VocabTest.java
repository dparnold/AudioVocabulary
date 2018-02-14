package com.dparnold.audiovocabulary;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dparnold.audiovocabulary.R;

import java.util.Collections;
import java.util.List;




public class VocabTest extends AppCompatActivity {
    private LinearLayout buttonLayout;
    private Button showButton;
    private Button fineButton;
    private Button againButton;
    private TextView displayVocable;
    private LinearLayout.LayoutParams buttonParams;
    private List<Vocable> vocables;
    private int counter;
    private int studyNumber;
    private AppDatabase db;
    private ProgressBar leftProgress;
    private ProgressBar rightProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_test);
        db = AppDatabase.getAppDatabase(this);



        buttonLayout = findViewById(R.id.buttonLayout);
        displayVocable = findViewById(R.id.displayVocable);
        buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);


        showButton = new Button(this);
        showButton.setLayoutParams(buttonParams);
        showButton.setText("show");
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(showButton);
            }
        });
        buttonLayout.addView(showButton);

        // Getting vocabulary
        counter=0;
        studyNumber=10;
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
        againButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                again(againButton);
            }
        });

        fineButton = new Button(this);
        fineButton.setLayoutParams(buttonParams);
        fineButton.setText("fine");
        fineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               fine(fineButton);
            }
        });

        buttonLayout.addView(againButton);
        buttonLayout.addView(fineButton);

        vocables.get(counter).setTimesStudied(vocables.get(counter).getTimesStudied()+1);

        counter+=1;
        if(counter >= studyNumber){
            counter = 0;
        }
    }

    public void fine(View view){
        displayVocable.setText(vocables.get(counter).getLang0());
        vocables.get(counter).setScore(vocables.get(counter).getScore()+1);
        db.vocableDAO().updateVocable(vocables.get(counter));
        buttonLayout.removeAllViews();
        buttonLayout.addView(showButton);

    }

    public void again(View view){
        displayVocable.setText(vocables.get(counter).getLang0());
        vocables.get(counter).setScore(vocables.get(counter).getScore()-2);
        db.vocableDAO().updateVocable(vocables.get(counter));
        buttonLayout.removeAllViews();
        buttonLayout.addView(showButton);
    }
}
