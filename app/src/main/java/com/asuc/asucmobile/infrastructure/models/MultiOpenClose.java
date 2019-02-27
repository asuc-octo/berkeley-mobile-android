package com.asuc.asucmobile.infrastructure.models;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class MultiOpenClose {
    @PropertyName("close_time")
    public ArrayList<Long> closeTimes;

    @PropertyName("open_time")
    public ArrayList<Long> openTimes;

    @PropertyName("type")
    public String type;
}
