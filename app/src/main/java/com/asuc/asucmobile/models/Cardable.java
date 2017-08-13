package com.asuc.asucmobile.models;

import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Cardable {

    static final SimpleDateFormat HOURS_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    public String getImageLink();

    public String getName();

    public String getTimes();

    public boolean isOpen();

}
