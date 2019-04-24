package com.asuc.asucmobile.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CalendarItem {
    private String date;
    private String info;
    private boolean isHeader;
}
