package com.dparnold.audiovocabulary;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dparnold.audiovocabulary.R;

public class Settings extends AppCompatActivity {
    // File where the settings are saved.
    public static final String SETTINGS_NAME = "AppSettings";
    // The Preferences object
    private SharedPreferences settings;

    // Different views that have the settings values
    private Switch testSwitch;
    private Spinner vocablesNumber;
    private EditText editTextDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        // Get settings / 0 signifies the standart operating mode
        settings = getSharedPreferences(SETTINGS_NAME, 0);

        // Set the values of the views to the settings values
        testSwitch =  findViewById(R.id.isittrue);
        testSwitch.setChecked(settings.getBoolean("testValue", false)); // Default value is false
        vocablesNumber = findViewById(R.id.spinner);
        vocablesNumber.setSelection(settings.getInt("vocablesNumber",1));
        editTextDelay =  findViewById(R.id.editTextDelay);
        editTextDelay.setText(String.valueOf(settings.getFloat("delay",(float)1.0)));

    }
    public void saveSettings(View view){
        // This method is called by the save button
        // Get an editor to change the settings
        SharedPreferences.Editor editor = settings.edit();

        // Write the values from the views to the editor
        editor.putBoolean("testValue", testSwitch.isChecked());
        editor.putFloat("delay",Float.valueOf(editTextDelay.getText().toString()));
        // Commit the edits
        editor.commit();

        // Send the user a message of success
        Toast.makeText(Settings.this,"Settings saved!",Toast.LENGTH_SHORT).show();
        // Go back to previous activity
        finish();
    }
    public void discard(View discardButton){
        // Just go back without saving anything
        finish();
    }
}
