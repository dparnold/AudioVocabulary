package com.dparnold.audiovocabulary;

import android.app.AlertDialog;
import android.arch.persistence.room.RoomDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dparnold.audiovocabulary.Helper.SpanishDict;
import com.dparnold.audiovocabulary.Views.PackageView;

import java.util.List;


public class VocablePackages extends AppCompatActivity {
    private LinearLayout packageList;
    private com.dparnold.audiovocabulary.AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocable_packages);
        db = com.dparnold.audiovocabulary.AppDatabase.getAppDatabase(this);

        packageList=findViewById(R.id.packageList);
        getVocablePackages();

        FloatingActionButton toAddPackageDialogButton = findViewById(R.id.toAddPackageDialog);
        toAddPackageDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPackageAlert();
            }
        });
    }
    private void openAddPackageAlert(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_vocable_package_dialog, null);
        // Create a new AlertDialog with my custom theme
        final android.app.AlertDialog alertDialog = new AlertDialog.Builder(VocablePackages.this, R.style.DialogTheme).create();
        // this is set the view from XML inside AlertDialog
        alertDialog.setView(alertLayout);
        alertDialog.show();
        final EditText textInput = alertLayout.findViewById(R.id.textInput);
        Button addButton = alertLayout.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpanishDict newDict = new SpanishDict();
                newDict.addVocabularyToDatabase(db,textInput.getText().toString());
                getVocablePackages();
                alertDialog.dismiss();

            }
        });
    }
    private void getVocablePackages (){
        packageList.removeAllViews();
        List<String> packageNames = db.vocableDAO().getPackageNames();
        for(int i=0;i<packageNames.size();i++){
            packageList.addView(new PackageView(this,db,packageNames.get(i)));
        }
    }
}
