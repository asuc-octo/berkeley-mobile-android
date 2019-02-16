package com.asuc.asucmobile.infrastructure.models;

import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

import javax.annotation.PropertyKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryLoc {

    @PropertyName("category")
    public String category;

    @PropertyName("details")
    public String details;

    @PropertyName("image_link")
    public String imageLink;

    @PropertyName("info_link")
    public String infoLink;

    @PropertyName("latitude")
    public double latitude;

    @PropertyName("longitude")
    public double longitude;

    @PropertyName("name")
    public String name;

    @PropertyName("notes")
    public String notes;

    @PropertyName("email")
    public String email;

    @PropertyName("on_campus")
    public boolean onCampus;

    // only some map icons have this e.g. printers
    @PropertyName("open_close")
    public ArrayList<OpenClose> openCloses;
}
