package com.dparnold.audiovocabulary.Views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.dparnold.audiovocabulary.R;
import com.dparnold.audiovocabulary.Settings;

public class PackageView extends ConstraintLayout {
    private TextView nameTextView, vocableNumberTextView;
    private Button studyButton, selectButton;
    private final String packageName;
    private SharedPreferences settings;



    //To allow Android Studio to interact with your view, at a minimum you must provide a constructor that takes a Context and an AttributeSet object as parameters. This constructor allows the layout editor to create and edit an instance of your view.
    public PackageView(Context context, com.dparnold.audiovocabulary.AppDatabase db, String packageNameInput) {
        super(context);
        //Inflate xml resource, pass "this" as the parent, we use <merge> tag in xml to avoid
        //redundant parent, otherwise a LinearLayout will be added to this LinearLayout ending up
        //with two view groups
        inflate(getContext(), R.layout.view_package,this);
        this.packageName = packageNameInput;
        settings = getContext().getSharedPreferences(Settings.SETTINGS_NAME, 0);
        nameTextView = this.findViewById(R.id.nameTextView);
        vocableNumberTextView = this.findViewById(R.id.vocableNumber);
        studyButton = this.findViewById(R.id.studyButton);
        selectButton = this.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("packageName", packageName);
                editor.commit();
                Activity activity = (Activity)getContext();
                activity.finish(); // go back to last activity
            }
        });
        nameTextView.setText(packageName);
        vocableNumberTextView.setText(String.valueOf(db.vocableDAO().countVocablesInPackage(packageName)));
    }
    private void setup(){

    }
}
