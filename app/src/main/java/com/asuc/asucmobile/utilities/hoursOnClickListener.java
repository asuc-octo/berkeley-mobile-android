package com.asuc.asucmobile.utilities;

import android.app.Activity;
import android.view.View;

/**
 * Created by Jason on 1/4/2016.
 */
public abstract class hoursOnClickListener extends Activity implements View.OnClickListener {
    public boolean status;

    public hoursOnClickListener() {
        super();
        status = false;
    }

    public abstract void onClick(View v);
}