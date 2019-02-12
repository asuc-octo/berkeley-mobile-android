package com.asuc.asucmobile.infrastructure.models;

import com.google.firebase.firestore.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenClose {

    @PropertyName("close_time")
    public long closeTime;

    @PropertyName("open_time")
    public long openTIme;

    @PropertyName("type")
    public String type;

}
