package com.dparnold.audiovocabulary.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by dominik on 1/31/18.
 */

public class VocableTableRow {
    public static TableRow getTableRow(Context context, int ID, String lang0, String lang1, int score, int timesStudied){
        TableRow tableRow = new TableRow(context);
        tableRow.setBackgroundColor(Color.parseColor("#000000"));
        tableRow.setPadding(1,0,1,1);

        TextView IDText = new TextView(context);
        IDText.setText(Integer.toString(ID));
        IDText.setPadding(16,16,16,0);
        IDText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(IDText);


        TextView lang0Text = new TextView(context);
        lang0Text.setText(lang0);
        lang0Text.setPadding(16,16,16,0);
        lang0Text.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(lang0Text);


        TextView lang1Text = new TextView(context);
        lang1Text.setText(lang1);
        lang1Text.setPadding(16,16,16,0);
        lang1Text.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(lang1Text);

        TextView scoreText = new TextView(context);
        scoreText.setText(String.valueOf(score));
        scoreText.setPadding(16,16,16,0);
        scoreText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(scoreText);

        TextView timesStudiedText = new TextView(context);
        timesStudiedText.setText(String.valueOf(timesStudied));
        timesStudiedText.setPadding(16,16,16,0);
        timesStudiedText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(timesStudiedText);
        return tableRow;
    }

    public static TableRow getHeaderTableRow(Context context, String ID, String lang0, String lang1, String score, String timesStudied){
        TableRow tableRow = new TableRow(context);
        tableRow.setBackgroundColor(Color.parseColor("#000000"));
        tableRow.setPadding(1,0,1,8);

        TextView IDText = new TextView(context);
        IDText.setTypeface(null, Typeface.BOLD);
        IDText.setText(ID);
        IDText.setPadding(16,16,16,0);
        IDText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(IDText);


        TextView lang0Text = new TextView(context);
        lang0Text.setTypeface(null, Typeface.BOLD);
        lang0Text.setText(lang0);
        lang0Text.setPadding(16,16,16,0);
        lang0Text.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(lang0Text);


        TextView lang1Text = new TextView(context);
        lang1Text.setTypeface(null, Typeface.BOLD);
        lang1Text.setText(lang1);
        lang1Text.setPadding(16,16,16,0);
        lang1Text.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(lang1Text);

        TextView scoreText = new TextView(context);
        scoreText.setTypeface(null, Typeface.BOLD);
        scoreText.setText(score);
        scoreText.setPadding(16,16,16,0);
        scoreText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(scoreText);

        TextView timesStudiedText = new TextView(context);
        timesStudiedText.setTypeface(null, Typeface.BOLD);
        timesStudiedText.setText(timesStudied);
        timesStudiedText.setPadding(16,16,16,0);
        timesStudiedText.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tableRow.addView(timesStudiedText);
        return tableRow;
    }
}
