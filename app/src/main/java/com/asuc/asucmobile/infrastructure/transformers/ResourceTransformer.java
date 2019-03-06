package com.asuc.asucmobile.infrastructure.transformers;

import android.util.Log;

import com.asuc.asucmobile.infrastructure.models.MultiOpenClose;
import com.asuc.asucmobile.infrastructure.models.OpenClose;
import com.asuc.asucmobile.infrastructure.models.Resource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResourceTransformer {

    public static final String TAG = "ResourceTransformer";

    /**
     * Transform Firebase QuerySnapshot to resource model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Resource> resourceQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Resource> resources = new ArrayList<>();
        Resource infraResource = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            try {
                infraResource = documentSnapshot.toObject(Resource.class);
                if (infraResource != null) {
                    resources.add(resourceInfraDomainTransformer(infraResource));
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return resources;
    }


    /**
     * Transform single resource from infrastructure to model
     * @param resource
     * @return
     */
    public com.asuc.asucmobile.domain.models.Resource resourceInfraDomainTransformer(Resource resource) {

        ArrayList<Date> weeklyOpen = new ArrayList<>();
        ArrayList<Date> weeklyClose = new ArrayList<>(); // TODO: generalize this
        ArrayList<Boolean> byAppt = new ArrayList<>();

        if (resource.getOpenCloses() != null) {
            for (OpenClose openClose : resource.getOpenCloses()) {
                weeklyOpen.add(new Date(openClose.getOpenTime() * 1000));
                weeklyClose.add(new Date(openClose.getCloseTime() * 1000));
                byAppt.add(false);
            }
        }

        return com.asuc.asucmobile.domain.models.Resource.builder()
                .location(resource.getAddress())
                .description(resource.getDescription())
                .email(resource.getEmail()) // TODO: do something with keycard
                .latitude(resource.getLatitude())
                .longitude(resource.getLongitude())
                .resource(resource.getName())
                .onOrOffCampus(resource.isOnCampus() ? "On campus" : "Off campus") // TODO: generalize this
                .weeklyOpen(weeklyOpen)
                .weeklyClose(weeklyClose)
                .weeklyAppointments(byAppt)
                .phone1(resource.getPhone()) // TODO: load in the picture
                .build();
    }

}
