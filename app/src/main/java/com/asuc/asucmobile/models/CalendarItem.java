package com.asuc.asucmobile.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CalendarItem {
    @Getter private String date;
    @Getter private String info;
    @Getter private boolean isHeader;
}
