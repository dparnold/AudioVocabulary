package com.dparnold.audiovocabulary.Views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dparnold.audiovocabulary.R;

public class PackageView extends ConstraintLayout {
    private TextView mFacebookTv,mYelpTv,mFoursquareTv;

    //To allow Android Studio to interact with your view, at a minimum you must provide a constructor that takes a Context and an AttributeSet object as parameters. This constructor allows the layout editor to create and edit an instance of your view.
    public PackageView(Context context){
        super(context);
        //Inflate xml resource, pass "this" as the parent, we use <merge> tag in xml to avoid
        //redundant parent, otherwise a LinearLayout will be added to this LinearLayout ending up
        //with two view groups
        inflate(getContext(), R.layout.view_package,this);

    }
}
