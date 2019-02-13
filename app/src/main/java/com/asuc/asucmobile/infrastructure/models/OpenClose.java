package com.asuc.asucmobile.infrastructure.models;

import com.google.firebase.firestore.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OpenClose {

    @PropertyName("close_time")
    public long closeTime;

    @PropertyName("open_time")
    public long openTIme;

    @PropertyName("type")
    public String type;

}
