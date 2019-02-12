package com.asuc.asucmobile.infrastructure.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Library {

    public int id;
    @PropertyName("name")
    public String name;

    @PropertyName("address")
    public String location;

    @PropertyName("phone_number")
    public String phone;
    @PropertyName("open_close")
    public ArrayList<OpenClose> openCloses;

    @PropertyName("latitude")
    public double latitude;

    @PropertyName("longtitude")
    public double longtitude;

    @PropertyName("picture")
    public String picture;

    @PropertyName("description")
    public String description;

}
