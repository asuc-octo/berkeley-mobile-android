package com.asuc.asucmobile.infrastructure.transformers;

import android.util.Log;

import com.asuc.asucmobile.infrastructure.models.GymClass;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GymClassTransformer {

    public static final String TAG = "GymClassTransformer";

    public List<com.asuc.asucmobile.domain.models.GymClass> gymClassQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.GymClass> gymClasses = new ArrayList<>();
        GymClass infraGymClass = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            try {
                infraGymClass = documentSnapshot.toObject(GymClass.class);
                if (infraGymClass != null) {
                    gymClasses.add(gymClassInfraDomainTransformer(infraGymClass));
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return gymClasses;
    }

    public com.asuc.asucmobile.domain.models.GymClass gymClassInfraDomainTransformer(GymClass gymClass) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYY-mm-dd"); // TODO: well this is ugly...
        Date startDate = new Date(gymClass.getStartTime() * 1000);
        Date endDate = new Date(gymClass.getEndTime() * 1000);
        return com.asuc.asucmobile.domain.models.GymClass.builder()
                .start(startDate)
                .end(endDate)
                .date(dateFormat.format(startDate))
                .name(gymClass.getName())
                .location(gymClass.getRoom())
                .trainer(gymClass.getTrainer())
                .classType(gymClass.getType())
                .build();
    }
}
