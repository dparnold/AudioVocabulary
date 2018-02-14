package com.dparnold.audiovocabulary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.dparnold.audiovocabulary.helper.ReadVocablePackage;
import com.dparnold.audiovocabulary.helper.VocableTableRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.y;
import static android.os.Build.ID;

public class VocableList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocable_list);
        TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
        AppDatabase db = AppDatabase.getAppDatabase(this);


        List<Vocable> vocables;
        vocables = db.vocableDAO().getAll();

        table.addView(VocableTableRow.getHeaderTableRow(this, "ID","English","Spanish","Score","Times studied"));

        for (int i = 0; i < vocables.size(); i++) {
            table.addView(VocableTableRow.getTableRow(this,
                    vocables.get(i).getID(),
                    vocables.get(i).getLang0(),
                    vocables.get(i).getLang1(),
                    vocables.get(i).getScore(),
                    vocables.get(i).getTimesStudied()
                    ));
        }




    }


}
