package com.asuc.asucmobile.infrastructure.transformers;

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

    /**
     * Transform Firebase QuerySnapshot to resource model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Resource> resourceQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Resource> resources = new ArrayList<>();
        Resource infraResource = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraResource = documentSnapshot.toObject(Resource.class);
            if (infraResource != null) {
                resources.add(resourceInfraDomainTransformer(infraResource));
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

        Calendar c = Calendar.getInstance();
        StringBuilder hoursString = new StringBuilder();
        SimpleDateFormat openDateformat = new SimpleDateFormat("EEEE hh:mm a"); // the day of the week spelled out completely
        SimpleDateFormat closeDateFormat = new SimpleDateFormat("- hh:mm a\n");

        for (OpenClose openClose : resource.getOpenCloses()) {
            Date openDate = new Date(openClose.getOpenTIme());
            Date closeDate = new Date(openClose.getCloseTime());
            hoursString.append(openDateformat.format(openDate));
            hoursString.append(closeDateFormat.format(closeDate));
        }

        return com.asuc.asucmobile.domain.models.Resource.builder()
                .location(resource.getAddress())
                .description(resource.getDescription())
                .email(resource.getEmail()) // TODO: do something with keycard
                .latitude(resource.getLatitude())
                .longitude(resource.getLongitude())
                .resource(resource.getName())
                .onOrOffCampus(resource.isOnCampus() ? "On campus" : "Off campus") // TODO: generalize this
                .hours(hoursString.toString())
                .phone1(resource.getPhone()) // TODO: load in the picture
                .build();
    }

}
