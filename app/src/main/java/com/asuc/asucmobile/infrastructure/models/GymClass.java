package com.asuc.asucmobile.infrastructure.models;

import com.google.firebase.firestore.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GymClass {

    @PropertyName("start_time")
    public long startTime;

    @PropertyName("end_time")
    public long endTime;

    @PropertyName("name")
    public String name;

    @PropertyName("room")
    public String room;

    @PropertyName("trainer")
    public String trainer;

    @PropertyName("type")
    public String type;
}
