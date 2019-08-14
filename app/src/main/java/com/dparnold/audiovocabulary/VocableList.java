package com.dparnold.audiovocabulary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;


import com.dparnold.audiovocabulary.Helper.VocableTableRow;

import java.util.List;

public class VocableList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocable_list);
        TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
        AppDatabase db = AppDatabase.getAppDatabase(this);


        List<Vocable> vocables;
        vocables = db.vocableDAO().getAll();

        table.addView(VocableTableRow.getHeaderTableRow(this, "ID","English","Spanish","Due","Score","#Studied", "PackageOrigin", "PackageName"));

        for (int i = 0; i < vocables.size(); i++) {
            table.addView(VocableTableRow.getTableRow(this,
                    vocables.get(i).getID(),
                    vocables.get(i).getLangKnown(),
                    vocables.get(i).getLangForeign(),
                    vocables.get(i).isToTest(),
                    vocables.get(i).getScore(),
                    vocables.get(i).getTimesStudied(),
                    vocables.get(i).getPackageOrigin(),
                    vocables.get(i).getPackageName()
                    ));
        }




    }


}
