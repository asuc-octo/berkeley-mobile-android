package com.asuc.asucmobile.infrastructure.transformers;

import android.util.Log;

import com.asuc.asucmobile.infrastructure.models.Library;
import com.asuc.asucmobile.infrastructure.models.MultiOpenClose;
import com.asuc.asucmobile.infrastructure.models.OpenClose;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.NonNull;

public class LibraryTransformer {

    public static final String TAG = "LibraryTransformer";

    /**
     * Transform Firebase QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Library> libraryQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Library> libraries = new ArrayList<>();
        Library infraLibrary = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            try {
                infraLibrary = documentSnapshot.toObject(Library.class);
                if (infraLibrary != null) {
                    libraries.add(libraryInfraDomainTransformer(infraLibrary));
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }
        return libraries;
    }

    /**
     * Transform single library from infrastructure to domain model
     * @param library
     * @return
     */
    public com.asuc.asucmobile.domain.models.Library libraryInfraDomainTransformer(@NonNull Library library) {

        ArrayList<Date> weeklyOpen = new ArrayList<>();
        ArrayList<Date> weeklyClose = new ArrayList<>();
        ArrayList<Boolean> byAppt = new ArrayList<>();


        if (library.getOpenCloses() != null) {
            for (OpenClose openClose : library.getOpenCloses()) {
                weeklyOpen.add(new Date(openClose.getOpenTime() * 1000));
                weeklyClose.add(new Date(openClose.getCloseTime() * 1000));
                byAppt.add(false);
            }
        }

        return com.asuc.asucmobile.domain.models.Library.builder()
                .id(library.getId())
                .name(library.getName())
                .location(library.getLocation())
                .phone(library.getPhone())
                .weeklyOpen(weeklyOpen)
                .weeklyClose(weeklyClose)
                .latitude(library.getLatitude())
                .longitude(library.getLongitude())
                .weeklyAppointments(byAppt)
                .build();
    }

}
